package com.dfsek.noise.swing.actions;

import com.dfsek.terra.api.util.mutable.MutableBoolean;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class MutableBooleanAction extends AbstractAction {
    private final MutableBoolean mutableBoolean;

    public MutableBooleanAction(MutableBoolean mutableBoolean, String action) {
        super(action);
        this.mutableBoolean = mutableBoolean;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        mutableBoolean.invert();
    }
}
