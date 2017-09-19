/*
 * MegaMekLab - Copyright (C) 2017 - The MegaMek Team
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 */
package megameklab.com.ui.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import megamek.common.EquipmentType;
import megamek.common.annotations.Nullable;
import megamek.common.util.EncodeControl;
import megameklab.com.ui.util.TechComboBox;

/**
 * Panel used to set armor value for a single location. Optionally used for rear location as well,
 * and can be used to set the armor type for units with patchwork armor.
 * 
 * @author Neoancient
 *
 */
public class ArmorLocationView extends MainUIView implements ActionListener, ChangeListener {
    
    /**
     * 
     */
    private static final long serialVersionUID = 6663440021651827007L;

    public interface ArmorLocationListener {
        void armorPointsChanged(int location, int front, int rear);
        void patchworkTypeChanged(int location, EquipmentType at);
    }
    private final List<ArmorLocationListener> listeners = new CopyOnWriteArrayList<>();
    public void addListener(ArmorLocationListener l) {
        listeners.add(l);
    }
    public void removeListener(ArmorLocationListener l) {
        listeners.remove(l);
    }
    
    private final SpinnerNumberModel spnPointsModel = new SpinnerNumberModel(0, 0, null, 1);
    private final SpinnerNumberModel spnPointsRearModel = new SpinnerNumberModel(0, 0, null, 1);
    private final JSpinner spnPoints = new JSpinner(spnPointsModel);
    private final JSpinner spnPointsRear = new JSpinner(spnPointsRearModel);
    private final TechComboBox<EquipmentType> cbArmorType = new TechComboBox<>(eq -> eq.getName());
    private final JLabel lblRear = new JLabel();
    private final JLabel lblMaxPoints = new JLabel();
    
    private final int location;
    private final String maxFormat;
    private Integer maxPoints;
    private boolean hasRear = false;
    
    ArmorLocationView(int location) {
        this.location = location;
        
        ResourceBundle resourceMap = ResourceBundle.getBundle("megameklab.resources.Views", new EncodeControl()); //$NON-NLS-1$
        lblRear.setText(resourceMap.getString("ArmorLocationView.lblRear.text")); //$NON-NLS-1$
        maxFormat = resourceMap.getString("ArmorLocationView.lblMax.format"); //$NON-NLS-1$
        setBorder(BorderFactory.createTitledBorder(
                null, "",
                TitledBorder.TOP,
                TitledBorder.DEFAULT_POSITION));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        setFieldSize(spnPoints, spinnerSize);
        add(spnPoints, gbc);
        gbc.gridy++;
        add(lblRear, gbc);
        gbc.gridy++;
        setFieldSize(spnPointsRear, spinnerSize);
        add(spnPointsRear, gbc);
        gbc.gridy++;
        add(lblMaxPoints, gbc);
        gbc.gridy++;
        gbc.weighty = 1.0;
        add(cbArmorType, gbc);
        spnPoints.addChangeListener(this);
        spnPointsRear.addChangeListener(this);
        cbArmorType.addActionListener(this);
        
    }
    
    /**
     * Changes the location name in the title and whether it has a rear armor location.
     * 
     * @param locName
     * @param rear
     */
    public void updateLocation(String locName, boolean rear) {
        ((TitledBorder)getBorder()).setTitle(locName);
        hasRear = rear;
        lblRear.setVisible(rear);
        spnPointsRear.setVisible(rear);
        if (!rear) {
            spnPointsRear.setValue(0);
        }
    }
    
    /**
     * @return The index (LOC_* constant) of the location managed by this view.
     */
    public int getLocationIndex() {
        return location;
    }
    
    /**
     * Sets the maximum number of armor points that can be assigned to this location.
     * A value of null indicates that there is no maximum.
     * 
     * @param max
     */
    public void setMaxPoints(@Nullable Integer max) {
        maxPoints = max;
        spnPointsModel.setMaximum(max);
        spnPointsRearModel.setMaximum(max);
        if (null == max) {
            lblMaxPoints.setVisible(false);
        } else {
            lblMaxPoints.setVisible(true);
            lblMaxPoints.setText(String.format(maxFormat, max));
        }
    }
    
    /**
     * Provide a list of armors available for this location if the unit has patchwork armor. A null
     * value for the list means that the unit does not have patchwork armor and the combo box should
     * be hidden. 
     * 
     * @param ats       The list of available armors if the unit has patchwork armor, or null if it does not.
     * @param mixedTech If the unit has patchwork armor, this indicates whether the values in the combo
     *                  box should distinguish between Clan and IS.
     */
    public void setPatchworkTypes(@Nullable List<EquipmentType> ats, boolean mixedTech) {
        if (null == ats) {
            cbArmorType.setVisible(false);
        } else {
            cbArmorType.removeActionListener(this);
            cbArmorType.removeAllItems();
            ats.forEach(at -> cbArmorType.addItem(at));
            cbArmorType.addActionListener(this);
            cbArmorType.setVisible(true);
            cbArmorType.showTechBase(mixedTech);
        }
    }
    
    /**
     * Sets the number of points for this location. If the location has rear armor, this sets only the front.
     * 
     * @param points
     */
    public void setPoints(int points) {
        spnPoints.removeChangeListener(this);
        if (null == maxPoints) {
            spnPoints.setValue(points);
        } else {
            spnPoints.setValue(Math.min(points, maxPoints));
            if (hasRear && (getPoints() + getPointsRear() > maxPoints)) {
                spnPointsRearModel.setValue(maxPoints - getPoints());
            }
        }
        spnPoints.addChangeListener(this);
    }
    
    /**
     * @return The number of points of armor for this location (front).
     */
    public int getPoints() {
        return spnPointsModel.getNumber().intValue();
    }

    /**
     * Sets the number of points of armor for this location in the rear.
     * 
     * @param points
     */
    public void setPointsRear(int points) {
        spnPointsRear.removeChangeListener(this);
        if (null == maxPoints) {
            spnPointsRear.setValue(points);
        } else {
            spnPointsRear.setValue(Math.min(points, maxPoints));
            if (getPoints() + getPointsRear() > maxPoints) {
                spnPointsModel.setValue(maxPoints - getPointsRear());
            }
        }
        spnPointsRear.addChangeListener(this);
    }
    
    /**
     * @return The number of points of rear armor in this location.
     */
    public int getPointsRear() {
        return spnPointsRearModel.getNumber().intValue();
    }
    
    /**
     * Sets the type of armor for this location in the case of units with patchwork armor.
     * 
     * @param at
     */
    public void setArmorType(EquipmentType at) {
        cbArmorType.removeActionListener(this);
        cbArmorType.setSelectedItem(at);
        cbArmorType.addActionListener(this);
        if ((cbArmorType.getSelectedIndex() < 0)
                && (cbArmorType.getModel().getSize() > 0)) {
            cbArmorType.setSelectedIndex(0);
        }
    }
    
    /**
     * Shows or hides the armor type combobox based on whether the user has selected patchwork armor.
     * 
     * @param patchwork
     */
    public void setPatchwork(boolean patchwork) {
        cbArmorType.setVisible(patchwork);
    }
    
    /**
     * @return The type of armor in this location for units with patchwork armor.
     */
    public EquipmentType getPatchworkType() {
        return (EquipmentType)cbArmorType.getSelectedItem();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        listeners.forEach(l -> l.armorPointsChanged(location, getPoints(), getPointsRear()));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        listeners.forEach(l -> l.patchworkTypeChanged(location, getPatchworkType()));
    }

}