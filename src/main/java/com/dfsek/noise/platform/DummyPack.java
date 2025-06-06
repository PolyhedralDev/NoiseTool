/*
 * This file is part of Terra.
 *
 * Terra is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Terra is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Terra.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.dfsek.noise.platform;

import ca.solostudios.strata.Versions;
import ca.solostudios.strata.version.Version;
import ca.solostudios.strata.version.VersionRange;
import com.dfsek.paralithic.eval.parser.Parser;
import com.dfsek.paralithic.eval.parser.Parser.ParseOptions;
import com.dfsek.tectonic.api.TypeRegistry;
import com.dfsek.tectonic.api.config.Configuration;
import com.dfsek.tectonic.api.config.template.annotations.Value;
import com.dfsek.tectonic.api.config.template.object.ObjectTemplate;
import com.dfsek.tectonic.api.loader.AbstractConfigLoader;
import com.dfsek.tectonic.api.loader.ConfigLoader;
import com.dfsek.tectonic.api.loader.type.TypeLoader;
import com.dfsek.terra.api.Platform;
import com.dfsek.terra.api.addon.BaseAddon;
import com.dfsek.terra.api.config.ConfigPack;
import com.dfsek.terra.api.config.ConfigType;
import com.dfsek.terra.api.config.Loader;
import com.dfsek.terra.api.event.events.config.pack.ConfigPackPostLoadEvent;
import com.dfsek.terra.api.event.events.config.pack.ConfigPackPreLoadEvent;
import com.dfsek.terra.api.noise.NoiseSampler;
import com.dfsek.terra.api.properties.Context;
import com.dfsek.terra.api.registry.CheckedRegistry;
import com.dfsek.terra.api.registry.OpenRegistry;
import com.dfsek.terra.api.registry.Registry;
import com.dfsek.terra.api.registry.key.RegistryKey;
import com.dfsek.terra.api.tectonic.ShortcutLoader;
import com.dfsek.terra.api.util.reflection.ReflectionUtil;
import com.dfsek.terra.api.util.reflection.TypeKey;
import com.dfsek.terra.api.world.biome.generation.BiomeProvider;
import com.dfsek.terra.api.world.chunk.generation.stage.GenerationStage;
import com.dfsek.terra.api.world.chunk.generation.util.provider.ChunkGeneratorProvider;
import com.dfsek.terra.config.fileloaders.FolderLoader;
import com.dfsek.terra.config.loaders.GenericTemplateSupplierLoader;
import com.dfsek.terra.config.loaders.config.BufferedImageLoader;
import com.dfsek.terra.registry.CheckedRegistryImpl;
import com.dfsek.terra.registry.OpenRegistryImpl;
import com.dfsek.terra.registry.ShortcutHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


/**
 * Represents a Terra configuration pack.
 */
public class DummyPack implements ConfigPack {
    private final Context context = new Context();
    private static final Logger logger = LoggerFactory.getLogger(DummyPack.class);

    private final AbstractConfigLoader abstractConfigLoader = new AbstractConfigLoader();
    private final ConfigLoader selfLoader = new ConfigLoader();

    private final Map<BaseAddon, VersionRange> addons;

    private final Map<Type, CheckedRegistryImpl<?>> registryMap = new HashMap<>();
    private final Map<Type, ShortcutHolder<?>> shortcuts = new HashMap<>();

    private final RegistryKey key;

    private final Loader loader;

    private final NoiseSampler noiseSampler;

    private final boolean useLetExpressions;

    public DummyPack(Platform platform, Configuration noise, boolean useLetExpressions) {
        this.loader = new FolderLoader(new File(".").toPath());
        this.useLetExpressions = useLetExpressions;

        register(selfLoader);
        platform.register(selfLoader);

        register(abstractConfigLoader);
        platform.register(abstractConfigLoader);


        this.addons = platform
                .getAddons()
                .entries()
                .stream()
                .collect(HashMap::new, (map, addon) -> map.put(addon, Versions.getVersionRange(addon.getVersion(), true, addon.getVersion(), true)), HashMap::putAll);


        platform.getEventManager().callEvent(
                new ConfigPackPreLoadEvent(this, template -> selfLoader.load(template, noise)));



        this.key = RegistryKey.of("noise", "noise");

        platform.getEventManager().callEvent(new ConfigPackPostLoadEvent(this, template -> selfLoader.load(template, noise)));

        ObjectTemplate<NoiseSampler> noiseSamplerObjectTemplate = new ObjectTemplate<>() {
            @Value(".")
            private NoiseSampler value;

            @Override
            public NoiseSampler get() {
                return value;
            }
        };

        this.noiseSampler = selfLoader.load(noiseSamplerObjectTemplate, noise).get();
    }

    public NoiseSampler getNoiseSampler() {
        return noiseSampler;
    }

    @Override
    public <T> DummyPack applyLoader(Type type, TypeLoader<T> loader) {
        abstractConfigLoader.registerLoader(type, loader);
        selfLoader.registerLoader(type, loader);
        return this;
    }

    @Override
    public <T> DummyPack applyLoader(Type type, Supplier<ObjectTemplate<T>> loader) {
        abstractConfigLoader.registerLoader(type, loader);
        selfLoader.registerLoader(type, loader);
        return this;
    }

    @Override
    public void register(TypeRegistry registry) {
        registry.registerLoader(BufferedImage.class, new BufferedImageLoader(loader, this));
        registryMap.forEach(registry::registerLoader);
        shortcuts.forEach(registry::registerLoader); // overwrite with delegated shortcuts if present
    }

    @Override
    public ConfigPack registerConfigType(ConfigType<?, ?> type, RegistryKey key, int priority) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<BaseAddon, VersionRange> addons() {
        return addons;
    }

    @Override
    public BiomeProvider getBiomeProvider() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> CheckedRegistry<T> getOrCreateRegistry(TypeKey<T> typeKey) {
        return (CheckedRegistry<T>) registryMap.computeIfAbsent(typeKey.getType(), c -> {
            OpenRegistry<T> registry = new OpenRegistryImpl<>(typeKey);
            selfLoader.registerLoader(c, registry);
            abstractConfigLoader.registerLoader(c, registry);
            logger.debug("Registered loader for registry of class {}", ReflectionUtil.typeToString(c));

            if (typeKey.getType() instanceof ParameterizedType param) {
                Type base = param.getRawType();
                if (base instanceof Class  // should always be true but we'll check anyways
                        && Supplier.class.isAssignableFrom((Class<?>) base)) { // If it's a supplier
                    Type supplied = param.getActualTypeArguments()[0]; // Grab the supplied type
                    if (supplied instanceof ParameterizedType suppliedParam) {
                        Type suppliedBase = suppliedParam.getRawType();
                        if (suppliedBase instanceof Class // should always be true but we'll check anyways
                                && ObjectTemplate.class.isAssignableFrom((Class<?>) suppliedBase)) {
                            Type templateType = suppliedParam.getActualTypeArguments()[0];
                            GenericTemplateSupplierLoader<?> loader = new GenericTemplateSupplierLoader<>(
                                    (Registry<Supplier<ObjectTemplate<Supplier<ObjectTemplate<?>>>>>) registry);
                            selfLoader.registerLoader(templateType, loader);
                            abstractConfigLoader.registerLoader(templateType, loader);
                            logger.debug("Registered template loader for registry of class {}", ReflectionUtil.typeToString(templateType));
                        }
                    }
                }
            }

            return new CheckedRegistryImpl<>(registry);
        });
    }

    @Override
    public List<GenerationStage> getStages() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Loader getLoader() {
        return loader;
    }

    @Override
    public String getAuthor() {
        return "0.1.0";
    }

    @Override
    public Version getVersion() {
        return Versions.getVersion(1, 0, 0);
    }

    @Override
    public ParseOptions getExpressionParseOptions() {
        return new ParseOptions(useLetExpressions);
    }

    @SuppressWarnings("unchecked,rawtypes")
    @Override
    public <T> ConfigPack registerShortcut(TypeKey<T> clazz, String shortcut, ShortcutLoader<T> loader) {
        ShortcutHolder<?> holder = shortcuts
                .computeIfAbsent(clazz.getType(), c -> new ShortcutHolder<>(getOrCreateRegistry(clazz)))
                .register(shortcut, (ShortcutLoader) loader);
        selfLoader.registerLoader(clazz.getType(), holder);
        abstractConfigLoader.registerLoader(clazz.getType(), holder);
        return this;
    }

    @Override
    public ChunkGeneratorProvider getGeneratorProvider() {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> CheckedRegistry<T> getRegistry(Type type) {
        return (CheckedRegistry<T>) registryMap.get(type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> CheckedRegistry<T> getCheckedRegistry(Type type) throws IllegalStateException {
        return (CheckedRegistry<T>) registryMap.get(type);
    }

    @Override
    public RegistryKey getRegistryKey() {
        return key;
    }

    @Override
    public Context getContext() {
        return context;
    }
}
