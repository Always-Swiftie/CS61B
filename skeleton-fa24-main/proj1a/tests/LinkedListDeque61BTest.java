import jh61b.utils.Reflection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import deque.*;

/** Performs some basic linked list tests. */
public class LinkedListDeque61BTest {

     @Test
     /* In this test, we have three different assert statements that verify that addFirst works correctly. */
    public void addFirstTestBasic() {
        Deque61B<String> lld1 = new LinkedListDeque61B<>();

        lld1.addFirst("back"); // after this call we expect: ["back"]
         assertThat(lld1.toList()).containsExactly("back").inOrder();

        lld1.addFirst("middle"); // after this call we expect: ["middle", "back"]
         assertThat(lld1.toList()).containsExactly("middle", "back").inOrder();

         lld1.addFirst("front"); // after this call we expect: ["front", "middle", "back"]
         assertThat(lld1.toList()).containsExactly("front", "middle", "back").inOrder();

         /* Note: The first two assertThat statements aren't really necessary. For example, it's hard
           to imagine a bug in your code that would lead to ["front"] and ["front", "middle"] failing,
            but not ["front", "middle", "back"].
          */
     }

     @Test
     /* In this test, we use only one assertThat statement. IMO this test is just as good as addFirstTestBasic.
        In other words, the tedious work of adding the extra assertThat statements isn't worth it. */
     public void addLastTestBasic() {
         Deque61B<String> lld1 = new LinkedListDeque61B<>();

         lld1.addLast("front"); // after this call we expect: ["front"]
         lld1.addLast("middle"); // after this call we expect: ["front", "middle"]
         lld1.addLast("back"); // after this call we expect: ["front", "middle", "back"]
        assertThat(lld1.toList()).containsExactly("front", "middle", "back").inOrder();
     }

     @Test
     /* This test performs interspersed addFirst and addLast calls. */
     public void addFirstAndAddLastTest() {
         Deque61B<Integer> lld1 = new LinkedListDeque61B<>();

         /* I've decided to add in comments the state after each call for the convenience of the
            person reading this test. Some programmers might consider this excessively verbose. */
         lld1.addLast(0);   // [0]
         lld1.addLast(1);   // [0, 1]
         lld1.addFirst(-1); // [-1, 0, 1]
         lld1.addLast(2);   // [-1, 0, 1, 2]
        lld1.addFirst(-2); // [-2, -1, 0, 1, 2]

         assertThat(lld1.toList()).containsExactly(-2, -1, 0, 1, 2).inOrder();
     }

    // Below, you'll write your own tests for LinkedListDeque61B.
    @Test
    public void isEmptyTest() {
        Deque61B<Integer> deque = new LinkedListDeque61B<>();

        assertThat(deque.isEmpty()).isTrue();
        deque.addFirst(1);
        assertThat(deque.isEmpty()).isFalse();
        deque.removeFirst();
        assertThat(deque.isEmpty()).isTrue();
    }

    @Test
    public void sizeTest() {
        Deque61B<String> deque = new LinkedListDeque61B<>();

        assertThat(deque.size()).isEqualTo(0);
        deque.addLast("a");
        deque.addFirst("b");
        assertThat(deque.size()).isEqualTo(2);
        deque.removeLast();
        assertThat(deque.size()).isEqualTo(1);
    }

    @Test
    public void getTest() {
        Deque61B<Integer> deque = new LinkedListDeque61B<>();
        deque.addLast(10);
        deque.addLast(20);
        deque.addLast(30);

        assertThat(deque.get(0)).isEqualTo(10);
        assertThat(deque.get(2)).isEqualTo(30);
        assertThat(deque.get(3)).isNull();
    }

    @Test
    public void getRecursiveTest() {
        Deque61B<String> deque = new LinkedListDeque61B<>();
        deque.addLast("a");
        deque.addLast("b");
        deque.addLast("c");

        assertThat(deque.getRecursive(1)).isEqualTo("b");
        assertThat(deque.getRecursive(3)).isNull();
    }

    @Test
    public void testEmptyDequeOperations() {
        Deque61B<Integer> deque = new LinkedListDeque61B<>();

        assertThat(deque.removeFirst()).isNull();
        assertThat(deque.removeLast()).isNull();
        assertThat(deque.get(0)).isNull();
        assertThat(deque.getRecursive(0)).isNull();

        assertThat(deque.isEmpty()).isTrue();
        assertThat(deque.size()).isEqualTo(0);
    }

    @Test
    public void testAddRemoveLoop() {
        Deque61B<Integer> deque = new LinkedListDeque61B<>();

        for (int i = 0; i < 10; i++) {
            deque.addLast(i);
        }

        for (int i = 0; i < 10; i++) {
            assertThat(deque.removeFirst()).isEqualTo(i);
        }

        assertThat(deque.isEmpty()).isTrue();
    }

    @Test
    public void testMixedAddRemove() {
        Deque61B<Integer> deque = new LinkedListDeque61B<>();

        deque.addFirst(2);   // [2]
        deque.addLast(3);    // [2, 3]
        deque.addFirst(1);   // [1, 2, 3]
        deque.addLast(4);    // [1, 2, 3, 4]

        assertThat(deque.removeFirst()).isEqualTo(1); // [2, 3, 4]
        assertThat(deque.removeLast()).isEqualTo(4);  // [2, 3]
        assertThat(deque.toList()).containsExactly(2, 3);
    }

    @Test
    public void testSingleElementOperations() {
        Deque61B<String> deque = new LinkedListDeque61B<>();

        deque.addFirst("only");
        assertThat(deque.size()).isEqualTo(1);
        assertThat(deque.get(0)).isEqualTo("only");

        assertThat(deque.removeLast()).isEqualTo("only");
        assertThat(deque.isEmpty()).isTrue();
    }

    @Test
    public void testOutOfBoundsAccess() {
        Deque61B<Character> deque = new LinkedListDeque61B<>();
        deque.addLast('a');
        deque.addLast('b');

        assertThat(deque.get(2)).isNull();         // 超范围
        assertThat(deque.getRecursive(-1)).isNull(); // 负索引
    }

    @Test
    public void testLargeScaleOperations() {
        Deque61B<Integer> deque = new LinkedListDeque61B<>();

        for (int i = 0; i < 1000; i++) {
            deque.addLast(i);
        }

        assertThat(deque.size()).isEqualTo(1000);
        assertThat(deque.get(999)).isEqualTo(999);
        assertThat(deque.getRecursive(1000)).isNull(); // 越界
    }



}