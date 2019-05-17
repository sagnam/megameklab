/*
 * MegaMekLab
 * Copyright (C) 2019 The MegaMek Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package megameklab.com.ui.supportvehicle;

import megamek.common.*;
import megameklab.com.ui.MegaMekLabMainUI;
import megameklab.com.ui.Vehicle.tabs.BuildTab;
import megameklab.com.ui.tabs.EquipmentTab;
import megameklab.com.ui.tabs.FluffTab;
import megameklab.com.ui.tabs.PreviewTab;
import megameklab.com.util.MenuBarCreator;
import megameklab.com.util.UnitUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Main window for support vehicle construction
 */
public class SVMainUI extends MegaMekLabMainUI {
    private JTabbedPane configPane = new JTabbedPane(SwingConstants.TOP);
    private SVStructureTab structureTab;
    private EquipmentTab equipmentTab;
    private PreviewTab previewTab;
    private SVBuildTab buildTab;
    private FluffTab fluffTab;
    private SVStatusBar statusbar;
    private JPanel masterPanel = new JPanel();

    public SVMainUI() {

        super();
        createNewUnit(Entity.ETYPE_SUPPORT_TANK, false, false);
        setTitle(getEntity().getChassis() + " " + getEntity().getModel() + ".blk");
        MenuBarCreator menubarcreator = new MenuBarCreator(this);
        setJMenuBar(menubarcreator);
        JScrollPane scroll = new JScrollPane();
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.setViewportView(masterPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        this.add(scroll);

        reloadTabs();
        setVisible(true);
        repaint();
        refreshAll();
        MechSummaryCache.getInstance();
    }

    @Override
    public void reloadTabs() {
        masterPanel.removeAll();
        configPane.removeAll();

        masterPanel.setLayout(new BorderLayout());

        statusbar = new SVStatusBar(this);
        structureTab = new SVStructureTab(this);
        equipmentTab = new EquipmentTab(this);
        buildTab = new SVBuildTab(this, equipmentTab);
        fluffTab = new FluffTab(this);
        structureTab.addRefreshedListener(this);
        equipmentTab.addRefreshedListener(this);
        buildTab.addRefreshedListener(this);
        fluffTab.setRefreshedListener(this);

        previewTab = new PreviewTab(this);

        configPane.addTab("Structure/Armor", structureTab);
        configPane.addTab("Equipment", equipmentTab);
        configPane.addTab("Assign Criticals", buildTab);
        configPane.addTab("Fluff", fluffTab);
        configPane.addTab("Preview", previewTab);

        masterPanel.add(configPane, BorderLayout.CENTER);
        masterPanel.add(statusbar, BorderLayout.SOUTH);

        refreshHeader();
        this.repaint();
    }

    @Override
    public void refreshAll() {
        structureTab.refresh();
        equipmentTab.refresh();
        buildTab.refresh();
        statusbar.refresh();
        previewTab.refresh();
        refreshHeader();
        repaint();
    }

    @Override
    public void refreshArmor() {

    }

    @Override
    public void refreshBuild() {
        buildTab.refresh();
    }

    @Override
    public void refreshEquipment() {
        equipmentTab.refresh();
    }

    @Override
    public void refreshTransport() {
        // not used for vees
    }

    @Override
    public void refreshHeader() {
        String title = getEntity().getChassis() + " " + getEntity().getModel()
                + ".blk";

        if (UnitUtil.validateUnit(getEntity()).length() > 0) {
            title += "  (Invalid)";
            setForeground(Color.red);
        } else {
            setForeground(Color.BLACK);
        }
        setTitle(title);
    }

    @Override
    public void refreshStatus() {
        statusbar.refresh();
    }

    @Override
    public void refreshStructure() {
        structureTab.refresh();
    }

    @Override
    public void refreshWeapons() {
    }

    @Override
    public void createNewUnit(long entityType, boolean isPrimitive, boolean isIndustrial, Entity oldEntity) {
        if (entityType == Entity.ETYPE_SUPPORT_VTOL) {
            setEntity(new SupportVTOL());
            getEntity().setMovementMode(EntityMovementMode.VTOL);
            ((SupportVTOL) getEntity()).setHasNoDualTurret(true);
            ((SupportVTOL) getEntity()).setHasNoTurret(true);
        } else if (entityType == Entity.ETYPE_FIXED_WING_SUPPORT) {
            setEntity(new FixedWingSupport());
        } else if (entityType == Entity.ETYPE_LARGE_SUPPORT_TANK){
            setEntity(new LargeSupportTank());
            getEntity().setWeight(51);
            getEntity().setMovementMode(EntityMovementMode.WHEELED);
            ((SupportTank) getEntity()).setHasNoDualTurret(true);
        } else {
            setEntity(new SupportTank());
            getEntity().setMovementMode(EntityMovementMode.WHEELED);
            ((SupportTank) getEntity()).setHasNoDualTurret(true);
        }
        if (entityType != Entity.ETYPE_LARGE_SUPPORT_TANK) {
            getEntity().setWeight(20);
        }

        getEntity().setEngine(new Engine(0, Engine.COMBUSTION_ENGINE,
                Engine.SUPPORT_VEE_ENGINE));

        getEntity().autoSetInternal();
        getEntity().setArmorType(EquipmentType.T_ARMOR_STANDARD);
        for (int loc = 0; loc < getEntity().locations(); loc++) {
            getEntity().initializeArmor(0, loc);
        }

        if (null == oldEntity) {
            getEntity().setChassis("New");
            getEntity().setModel("Support Tank");
            getEntity().setYear(3145);
            getEntity().setStructuralTechRating(ITechnology.RATING_D);
            getEntity().setOriginalWalkMP(1);
        } else {
            getEntity().setChassis(oldEntity.getChassis());
            getEntity().setModel(oldEntity.getModel());
            getEntity().setYear(Math.max(oldEntity.getYear(),
                    getEntity().getConstructionTechAdvancement().getIntroductionDate()));
            getEntity().setSource(oldEntity.getSource());
            getEntity().setManualBV(oldEntity.getManualBV());
            SimpleTechLevel lvl = SimpleTechLevel.max(getEntity().getStaticTechLevel(),
                    SimpleTechLevel.convertCompoundToSimple(oldEntity.getTechLevel()));
            getEntity().setTechLevel(lvl.getCompoundTechLevel(oldEntity.isClan()));
            getEntity().setMixedTech(oldEntity.isMixedTech());
            getEntity().setMovementMode(oldEntity.getMovementMode());
            getEntity().setStructuralTechRating(oldEntity.getStructuralTechRating());
            getEntity().setArmorTechRating(oldEntity.getArmorTechRating());
            getEntity().setEngineTechRating(oldEntity.getEngineTechRating());
            getEntity().setOriginalWalkMP(oldEntity.getOriginalWalkMP());
        }
        getEntity().recalculateTechAdvancement();
    }

    @Override
    public void refreshPreview() {
        previewTab.refresh();
    }

    @Override
    public void refreshSummary() {
        structureTab.refreshSummary();
    }

    @Override
    public void refreshEquipmentTable() {
        equipmentTab.refreshTable();
    }

    @Override
    public ITechManager getTechManager() {
        return structureTab.getTechManager();
    }
}