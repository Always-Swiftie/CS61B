import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UnionFind {
    // TODO: Instance variables

    int[] array;

    /* Creates a UnionFind data structure holding N items. Initially, all
       items are in disjoint sets. */
    public UnionFind(int N) {
        // TODO: YOUR CODE HERE
        this.array=new int[N];
        //初始时每个索引处的值都应该为-1，表示当前单个节点作为一棵树的根
        Arrays.fill(array,-1);
    }

    /* Returns the size of the set V belongs to. */
    public int sizeOf(int v) {
        // TODO: YOUR CODE HERE
        int root=find(v);
        return Math.abs(array[root]);
    }

    /* Returns the parent of V. If V is the root of a tree, returns the
       negative size of the tree for which V is the root. */
    public int parent(int v) {
        // TODO: YOUR CODE HERE
        return array[v];
    }

    /* Returns true if nodes/vertices V1 and V2 are connected. */
    public boolean connected(int v1, int v2) {
        // TODO: YOUR CODE HERE
        //对v1,v2都执行一次find即可，看看结果是否相等
        return find(v1)==find(v2);
    }

    /* Returns the root of the set V belongs to. Path-compression is employed
       allowing for fast search-time. If invalid items are passed into this
       function, throw an IllegalArgumentException. */
    public int find(int v) {
        // TODO: YOUR CODE HERE
        if(v<0||v>=array.length){
            throw new IllegalArgumentException("illegal index");
        }
        /*
        思路：对于给定的v，如果array[v]为负数，说明v已经是根节点，直接返回
             如果array[v]不为负数，说明需要继续往上寻找，比如array[4]=3,4的父结点是3,在为4寻找根节点的时候我们经过了3，就可以执行路径压缩，把最后找到的根节点都放到路径经过的所有节点
             比如找到find(4)=0,那么array[4]=0,array[3]=0,array[2]=0 ....,
             是否需要一个辅助数组记录在对v找根的途中经过的元素？
             4找到根之后，根据记录下来的路过节点对他们一个个赋值？
         */
        //case1:already root
        if(array[v]<0){
            return v;
        }
        Set<Integer> path=new HashSet<>();//先使用一个集合记录经过的元素，最后对集合里面的索引处改变值就
        int parent=parent(v);
        while (array[parent]>=0){//当父节点还不为根时，就继续找
            path.add(parent);//记录当前路径经过节点
            parent=parent(parent);
        }
        //此时array[parent]应该为负数了，parent就是root,把刚才记录的路径上的节点的值都替换为parent
        for(Integer index:path){
            array[index]=parent;
        }
        return parent;
    }

    /* Connects two items V1 and V2 together by connecting their respective
       sets. V1 and V2 can be any element, and a union-by-size heuristic is
       used. If the sizes of the sets are equal, tie break by connecting V1's
       root to V2's root. Union-ing an item with itself or items that are
       already connected should not change the structure. */
    public void union(int v1, int v2) {
        if(v1<0||v1>=array.length||v2<0||v2>=array.length){
            return;
        }
        //思路：执行union操作的思路是，先分别找到两个节点所属树的根节点是谁，通过根节点获悉当前树的大小是多少，然后满足较小根的父节点成为较大集合的根节点，较大根的值相应地更新为新的大小
        int root1=find(v1);
        int root2=find(v2);
        if(root1==root2){
            return;//二者已经connected了
        }
        if(array[root1]==array[root2]){
            //如果两树的大小相等，把两个根连接起来即可
            array[root2]=array[root2]+array[root1];//先更新大根的值
            array[root1]=root2;
        }else if(array[root1]>array[root2]){
            array[root2]=array[root2]+array[root1];//先更新大根的值
            array[root1]=root2;
        }else if(array[root1]<array[root2]){
            array[root1]=array[root1]+array[root2];
            array[root2]=root1;
        }
    }
}
