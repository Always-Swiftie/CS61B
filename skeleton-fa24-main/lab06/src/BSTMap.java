import org.checkerframework.checker.units.qual.K;
import org.dom4j.Node;

import java.util.*;

//泛型Key必须是可比较的，所以必须extends Comparable<K>，以便使用compare方法
public class BSTMap <K extends Comparable<K>,V> implements Map61B<K,V> {

    //节点内部类
    private static class BSTNode<K,V>{
        K key;
        V value;
        BSTNode<K,V> leftChild;
        BSTNode<K,V> rightChild;
        //构造方法
        public BSTNode(K key,V value){
            this.key=key;
            this.value=value;
        }
        public BSTNode(K key,V value,BSTNode<K,V> leftChild,BSTNode<K,V> rightChild){
            this.key=key;
            this.value=value;
            this.leftChild=leftChild;
            this.rightChild=rightChild;
        }
    }
    BSTNode<K,V> root;
    int size=0;
    @Override
    /*
    put方法，将新的键值对存入树中，要分两种情况：
    1.输入的键key在树中已经存在-则把key对应的值更新为新的值
    2.输入的key在树中不存在，指针指向了null，为了把新创建的节点和它的父结点连接起来，
    我们需要创建多一个变量用于记录节点的前驱节点，当创建新的节点之后，根据值的大小判断成为前驱节点的左孩子还是右孩子
     */
    public void put(K key, V value) {
        if(root==null){//如果树是空的，那么创建新的根节点
            root=new BSTNode<>(key,value);
            size++;
            return;
        }
        BSTNode<K,V> parent=null;//前驱一开始为null
        BSTNode<K,V> p=root;
        while (p!=null){
            parent=p;//先更新父结点
            if(key.compareTo(p.key)<0){
                p=p.leftChild;
            }else if(key.compareTo(p.key)>0){
                p=p.rightChild;
            }else{
                //如果已经存在key，更新value即可
                p.value=value;
                return;
            }
        }
        //能走到这里，说明树中暂时没有对应的key，那么要新建节点，并与父结点建立连接
        if(key.compareTo(parent.key)<0){//作为父结点的左孩子
            parent.leftChild=new BSTNode<>(key,value);
        }else{//作为父结点的右孩子
            parent.rightChild=new BSTNode<>(key,value);
        }
        size++;//新增节点了，要更新size
    }
    @Override
    //get方法，根据索引查询值  规则：定义指针p从根节点开始，每次把待查索引与它的key做比较，如果待查<key,就去左子树；反之就去右子树 如果key相等，说明找到了，返回值即可；如果p等于null了，说明在整个树中没有找到匹配的key，没有找到，返回null

    public V get(K key) {
        if(root==null){
            return null;
        }
        BSTNode<K,V> p=root;
        while (p!=null){
            if(key.compareTo(p.key)<0){//待查key小于当前节点key,向左找
                p=p.leftChild;
            }else if(key.compareTo(p.key)>0){//待查key大于当前节点key,向左找
                p=p.rightChild;
            }else{//key==p.key，found！
                return p.value;
            }
        }
        //如果能走到这里，说明在整棵树中没有找到匹配的key,返回
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        if(root==null){
            return false;
        }
        BSTNode<K,V> p=root;
        while (p!=null){
            if (key.compareTo(p.key)<0){
                p=p.leftChild;
            }else if(key.compareTo(p.key)>0){
                p=p.rightChild;
            }else{
                return true;
            }
        }
        //能走到这，肯定没找到了
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        this.root=null;//first attempt
        this.size=0;
    }

    @Override
    /*
    要找到树里面所有的键值对，使用层序遍历
     */
    public Set<K> keySet() {
        if(root==null){
            return null;
        }
        Set<K> keys=new TreeSet<>();
        BSTNode<K,V> p=root;
        //层序遍历
        Queue<BSTNode<K,V>> nodeQueue=new LinkedList<>();
        nodeQueue.offer(root);
        while (!nodeQueue.isEmpty()){
            BSTNode<K,V> polled=nodeQueue.poll();
            if(polled!=null) {
                keys.add(polled.key);
                if (polled.leftChild!=null){
                    nodeQueue.offer(polled.leftChild);
                }
                if(polled.rightChild!=null){
                    nodeQueue.offer(polled.rightChild);
                }
            }
        }
        return keys;
    }

    @Override
    public V remove(K key) {
        //现在是树中只有根节点，没有孩子，删除出了问题
        if(root==null){
            return null;
        }
        BSTNode<K,V> parent=null;
        BSTNode<K,V> p=root;
        //先找到待删除节点
        while (p!=null){
            if(key.compareTo(p.key)<0){
                parent=p;
                p=p.leftChild;
            }else if(key.compareTo(p.key)>0){
                parent=p;
                p=p.rightChild;
            }else {
                //找到待删除节点了
                /*case1:待删除节点没有左孩子——把右孩子托孤给它的parent--
                待删除节点原来是左孩子或是右孩子，托孤上去的就是什么孩子
                 */
                if(p==root&&p.leftChild==null&&p.rightChild==null){
                    root=null;
                    size=0;
                    return p.value;
                }
                if(p.leftChild==null&&p.rightChild!=null){
                    shift(parent,p,p.rightChild);
                }else if(p.leftChild!=null&&p.rightChild==null){
                    //case2:待删除节点只有左孩子
                    shift(parent,p,p.leftChild);
                }else if(p.leftChild!=null&&p.rightChild!=null){//case3:待删除节点左右孩子都有，那么要先找到他的后继
                    //后继是它的右子树里面最小的
                    //同时需要记录后继的父结点，最后要用
                    BSTNode<K,V> s=p.rightChild;
                    BSTNode<K,V> sparent=p;
                    while (s.leftChild!=null){
                        sparent=s;
                        s=s.leftChild;
                    }
                    //成功找到后继
                    //case3-a:后继与待删除节点不相邻
                    if(sparent!=p){
                        //s绝对没有左孩子！
                        shift(sparent,s,s.rightChild);
                        s.rightChild=p.rightChild;
                    }
                    //case3-b:后继与待删除节点是相邻的
                    shift(parent,p,s);
                    s.leftChild=p.leftChild;
                }
                size--;//执行完删除，更新size
                return p.value;
            }
        }
        return null;
    }

    private void shift(BSTNode<K,V> parent,BSTNode<K,V> deleted,BSTNode<K,V> child){
        //shift托孤方法
        if(parent==null){
            this.root=child;
        }else if(deleted==parent.leftChild){
            parent.leftChild=child;
        }else{
            parent.rightChild=child;
        }
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException("Sorry");
    }
}
