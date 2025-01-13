package simplexmetod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimplexTableManager {
    private Set<SimplexMethod> simplexMethodSet = new HashSet<>();
    private List<SimplexMethod> simplexMethodList = new ArrayList<>();


    public void addTable(SimplexMethod table) {

        Matrix matrix = new Matrix(copyMatrix(table));
        SimplexMethod updateTable = new SimplexMethod(matrix, table.getIsBasic(), table.getIsFree());

        if (simplexMethodSet.add(updateTable)) {
            simplexMethodList.add(updateTable);
        }

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

    public Fraction[][] copyMatrix(SimplexMethod table) {
        Fraction[][] copy = new Fraction[table.getMatrix().getRows()][];

        for (int i = 0; i < table.getMatrix().getRows(); i++) {
            copy[i] = new Fraction[table.getMatrix().getCols()];
            System.arraycopy(table.getMatrix().getRowFromMatrix(i), 0, copy[i], 0, table.getMatrix().getCols());
        }

        return copy;
    }

}
