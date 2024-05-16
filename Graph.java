import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Graph{

    private HashMap<Integer,List<Integer>> adjacList;
    private List<int[]> listOfEdge;

    public Graph(){
        adjacList = new HashMap<>();
        listOfEdge = new ArrayList<>();
    }

    public Graph(List<int[]> listOfEdge){
        adjacList = new HashMap<>();
        this.listOfEdge = listOfEdge;
        for(int i = 0; i < listOfEdge.size(); i++){
            for (int j = 0; j < listOfEdge.get(i).length; j++) {
                int[] elements = (int[])listOfEdge.get(i);
                if (!adjacList.containsKey(elements[0])) {
                    adjacList.put(elements[0], new ArrayList<>());
                }
                if (!adjacList.containsKey(elements[1])) {
                    adjacList.put(elements[1], new ArrayList<>());
                }
                if (!adjacList.get(elements[1]).contains(elements[0])) {
                    adjacList.get(elements[1]).add(elements[0]);
                }
                if (!adjacList.get(elements[0]).contains(elements[1])) {
                    adjacList.get(elements[0]).add(elements[1]);
                }
            }
        }
        for (Integer keys : adjacList.keySet()) {
            Collections.sort(adjacList.get(keys));
        }
    }

    public List<Integer> getAdjVertices(Integer key){
        return adjacList.get(key);
    }

    public String getHamiltonCycle(){
        List<Integer> visited = new ArrayList<>();
        for (Integer integer : adjacList.keySet()) {
            visited.add(integer);
            boolean isAvail = getHamiltonCycle_(visited, integer, integer);
            if (isAvail) {
                String res = visited.stream().map(n -> String.valueOf(n)).collect(Collectors.joining("-"));
                return res;
            }
            visited.remove(visited.indexOf(integer));
        }
        return "Not possible";
    }

    private boolean getHamiltonCycle_(List<Integer> visited, int searching, int origin){
        List<Integer> neighbors = getAdjVertices(searching);
        if (visited.size() == adjacList.size()) {
            if (neighbors.contains(origin)) {
                visited.add(origin);
                return true;
            }
        }
        for (int i = 0; i < neighbors.size(); i++) {
            int element = neighbors.get(i);
            if (!visited.contains(element)) {
                visited.add(element);
                boolean res = getHamiltonCycle_(visited,element,origin);
                if (res) {
                    return res;
                }
                visited.remove(visited.indexOf(element));
            }
        }
        return false;
    }

    public static void main(String[] args) {
        List<int[]> test = new ArrayList<>();
        // int[][] arr = {{0,1},{0,3},{1,2},{3,1},{4,1},{4,2},{3,4}};
        int[][] arr1 = {{1,2},{1,7},{1,6},{2,6},{2,3},{2,8},{2,9},{3,8},{3,4},{4,8},{4,5},{4,9},{5,7},{5,6},{6,9}};
        for (int i = 0; i < arr1.length; i++) {
            test.add(arr1[i]);
        }
        Graph G = new Graph(test);
        System.out.println(G.getHamiltonCycle());
    }
}