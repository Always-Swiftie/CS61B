package deque;

import java.util.ArrayList;
import java.util.List;

public class LinkedListDeque61B<T> implements Deque61B<T> {

    private class Node{
        Node prev;
        T value;
        Node next;

        public Node(Node prev,T value,Node next){
            this.prev=prev;
            this.value=value;
            this.next=next;
        }

    }
    Node sentinel;//设置一个哨兵节点，同时作为链表的虚拟头和虚拟尾
    int size;

    public LinkedListDeque61B(){
        this.sentinel=new Node(null,null, null);//初始构造时哨兵的前后都是自己
        circle();
        this.size=0;
    }

    private void circle(){
        this.sentinel.next=sentinel;
        this.sentinel.prev=sentinel;
    }
    @Override
    public void addFirst(T x) {
        Node a=sentinel;
        Node b=sentinel.next;
        Node added=new Node(a,x,b);
        a.next=added;
        b.prev=added;
        size++;
    }

    @Override
    public void addLast(T x) {
        Node a=sentinel.prev;
        Node b=sentinel;
        Node added=new Node(a,x,b);
        a.next=added;
        b.prev=added;
        size++;
    }

    @Override
    public List<T> toList() {
        List<T> returnList=new ArrayList<>();
        //把我们的双端队列中的所有元素按照顺序放入List中
        Node cur=sentinel.next;
        while (cur!=sentinel){
            returnList.add(cur.value);
            cur=cur.next;
        }
        return returnList;
    }

    @Override
    public boolean isEmpty() {
        return this.size==0||this.sentinel.next==sentinel;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public T removeFirst() {
        if(isEmpty()){
            return null;
        }
        Node removed=sentinel.next;
        Node a=sentinel;
        Node b=removed.next;
        a.next=b;
        b.prev=a;
        size--;
        return removed.value;
    }

    @Override
    public T removeLast() {
        if(isEmpty()) {
            return null;
        }
        Node removed=sentinel.prev;
        Node a=removed.prev;
        Node b=removed.next;
        b.prev=a;
        a.next=b;
        size--;
        return removed.value;
    }

    @Override
    public T get(int index) {
        if(isEmpty()||index<0){
            return null;
        }
        Node cur=sentinel.next;
        int i=0;
        while (cur!=sentinel){
            if(i==index){
                return cur.value;
            }
            cur=cur.next;
            i++;
        }
        return null;
    }

    @Override
    public T getRecursive(int index) {
        if(isEmpty()||index<0){
            return null;
        }
        int i=0;
        return getRe(sentinel.next,index,i);
    }
    private T getRe(Node cur,int index,int i){
        if(i==index&&cur!=sentinel){
            return cur.value;
        }else if(i!=index){
            return getRe(cur.next,index,i+1);
        }
        return null;
    }
}
