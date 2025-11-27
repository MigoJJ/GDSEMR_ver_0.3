package com.emr.gds.main.medication.db;

import com.emr.gds.main.medication.model.MedicationGroup;
import com.emr.gds.main.medication.model.MedicationItem;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.InputStream;
import java.util.*;

public class DatabaseManager {
    private boolean pendingChanges = false;
    private Map<String, List<MedicationGroup>> cachedData = null;
    private List<String> cachedCategories = null;

    public void createTables() {
        // Mock implementation
    }

    public void ensureSeedData() {
        // Mock implementation
    }

    public List<String> getOrderedCategories() {
        if (cachedCategories == null) {
            loadData();
        }
        return cachedCategories;
    }

    public Map<String, List<MedicationGroup>> getMedicationData() {
        if (cachedData == null) {
            loadData();
        }
        return cachedData;
    }

    private void loadData() {
        cachedData = new HashMap<>();
        cachedCategories = new ArrayList<>();

        try (InputStream is = getClass().getResourceAsStream("/com/emr/gds/main/medication/med_data.xml")) {
            if (is == null) {
                System.err.println("Could not find med_data.xml");
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            NodeList catList = doc.getElementsByTagName("category");
            for (int i = 0; i < catList.getLength(); i++) {
                Node catNode = catList.item(i);
                if (catNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element catElement = (Element) catNode;
                    String categoryName = catElement.getAttribute("name");
                    cachedCategories.add(categoryName);

                    List<MedicationGroup> groups = new ArrayList<>();
                    NodeList groupList = catElement.getElementsByTagName("group");

                    for (int j = 0; j < groupList.getLength(); j++) {
                        Node groupNode = groupList.item(j);
                        if (groupNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element groupElement = (Element) groupNode;
                            String groupName = groupElement.getAttribute("name");

                            List<MedicationItem> items = new ArrayList<>();
                            NodeList itemList = groupElement.getElementsByTagName("item");

                            for (int k = 0; k < itemList.getLength(); k++) {
                                Node itemNode = itemList.item(k);
                                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                                    String itemText = itemNode.getTextContent().trim();
                                    items.add(new MedicationItem(itemText));
                                }
                            }
                            groups.add(new MedicationGroup(groupName, items));
                        }
                    }
                    cachedData.put(categoryName, groups);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to empty if error
            cachedCategories = new ArrayList<>();
            cachedData = new HashMap<>();
        }
    }

    public boolean hasPendingChanges() {
        return pendingChanges;
    }

    public void markDirty() {
        this.pendingChanges = true;
    }

    public void commitPending() {
        pendingChanges = false;
    }

    public void removeItem(MedicationItem item) {
        if (cachedData == null) return;
        for (List<MedicationGroup> groups : cachedData.values()) {
            for (MedicationGroup group : groups) {
                if (group.medications().remove(item)) {
                    markDirty();
                    return;
                }
            }
        }
    }

    public void addItem(String category, String groupName, MedicationItem item) {
        if (cachedData == null) return;
        List<MedicationGroup> groups = cachedData.get(category);
        if (groups != null) {
            for (MedicationGroup group : groups) {
                if (group.title().equals(groupName)) {
                    group.medications().add(item);
                    markDirty();
                    return;
                }
            }
        }
    }
}