package simplexmetod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimplexTableManager {

    private static int idCounter = 0;
    private int id;
    private Set<SimplexMethod> simplexMethodSet = new HashSet<>();
    private List<SimplexMethod> simplexMethodList = new ArrayList<>();

    public void addTable(SimplexMethod table) {
        if (simplexMethodSet.add(table)) {
            simplexMethodList.add(table);
        }

        System.out.println("Размер множества: " + simplexMethodSet.size());
        System.out.println("Размер списка: " + simplexMethodList.size());
    }

    public Set<SimplexMethod> getTables() {
        return simplexMethodSet;
    }

    public List<SimplexMethod> getList() {
        return simplexMethodList;
    }

    public void printAllTables() {
        for (SimplexMethod method : simplexMethodSet) {
            method.printTable();
        }
    }

    public SimplexMethod getLastTable() {
        if (!simplexMethodList.isEmpty()) {
            return simplexMethodList.get(simplexMethodList.size() - 1);
        }
        return null;
    }

    public SimplexMethod removeLastTable() {
        if (!simplexMethodList.isEmpty()) {
            SimplexMethod lastTable = simplexMethodList.remove(simplexMethodList.size() - 1);
            simplexMethodSet.remove(lastTable);
            return lastTable;
        }
        return null;
    }

    public SimplexTableManager add(SimplexTableManager other) {
        if (other != null) {
            simplexMethodSet.addAll(other.getTables());

            simplexMethodList.addAll(other.getList());
        }
        return this;
    }

}
