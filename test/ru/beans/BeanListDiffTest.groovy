package ru.beans

import org.junit.Test
import static ru.beans.Bean.beans
import static ru.beans.Bean.bean

/**
 * User: dima
 * Date: 22/2/11
 */
class BeanListDiffTest {
  @Test public void exactlySameBeansShouldHaveEmptyDiff_EvenWhenOrderedDifferently() {
    def diff = BeanListDiff.diff(
            beans([a: "A", b: 1], [a: "AA", b: 2]),
            beans([a: "AA", b: 2], [a: "A", b: 1]),
            ["a"], ["b"]
    )
    assert diff.left == []
    assert diff.diff == []
    assert diff.right == []
  }

  @Test public void differentBeansShouldHaveNotEmptyDiff() {
    def diff = BeanListDiff.diff(
            beans([a: "A", b: 1], [a: "AA", b: 2], [a: "AAA", b: 3]),
            beans([a: "AA", b: 222], [a: "AAA", b: 3], [a: "AAAA", b: 4], [a: "AAAAA", b: 5]),
            ["a"], ["b"]
    )
    assert diff.left == [bean([a: "A", b: 1])]
    assert diff.right == [bean([a: "AAAAA", b: 5]), bean([a: "AAAA", b: 4])]
    assert diff.diff == [ [[], ["b"], [], bean([a: "AA", b: 2]), bean([a: "AA", b: 222])] as BeanDiff ]
  }

  @Test public void whatShouldBeInDiffWhenThereAreDifferentAmountOfBeansWithTheSameKey() {

  }
}
