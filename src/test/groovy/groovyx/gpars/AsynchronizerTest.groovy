//  GPars (formerly GParallelizer)
//
//  Copyright © 2008-9  The original author or authors
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License. 

package groovyx.gpars

import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author Vaclav Pech
 * Date: Oct 23, 2008
 */
public class AsynchronizerTest extends GroovyTestCase {
  public void testStartInParallel() {
    Asynchronizer.withAsynchronizer(5) {ExecutorService service ->
      def resultA = 0, resultB = 0
      final CountDownLatch latch = new CountDownLatch(2)
      AsyncInvokerUtil.startInParallel({resultA = 1; latch.countDown()}, {resultB = 1; latch.countDown()})
      latch.await()
      assertEquals 1, resultA
      assertEquals 1, resultB
    }
  }

  public void testDoInParallel() {
    assertEquals([10, 20], AsyncInvokerUtil.doInParallel({10}, {20}))
  }

  public void testExecuteInParallel() {
    assertEquals([10, 20], AsyncInvokerUtil.executeInParallel({10}, {20})*.get())
  }

  public void testAsyncWithCollectionAndResult() {
    Asynchronizer.withAsynchronizer(5) {ExecutorService service ->
      Collection<Future> result = [1, 2, 3, 4, 5].collect({it * 10}.async())
      assertEquals(new HashSet([10, 20, 30, 40, 50]), new HashSet((Collection) result*.get()))
    }
  }

  public void testEachAsync() {
    def result = Collections.synchronizedSet(new HashSet())
    Asynchronizer.withAsynchronizer(5) {ExecutorService service ->
      [1, 2, 3, 4, 5].eachAsync {Number number -> result.add(number * 10)}
      assertEquals(new HashSet([10, 20, 30, 40, 50]), result)
    }
  }

  public void testEachAsyncOnSingleElementCollections() {
    Asynchronizer.withAsynchronizer(5) {ExecutorService service ->
      [1].eachAsync {}
      [1].eachAsync {}
      [1].eachAsync {}
      'a'.eachAsync {}
      [1].iterator().eachAsync {}
      'a'.iterator().eachAsync {}
    }
  }

  public void testEachAsyncOnEmpty() {
    Asynchronizer.withAsynchronizer(5) {ExecutorService service ->
      [].eachAsync {throw new RuntimeException('Should not be thrown')}
      [].eachAsync {throw new RuntimeException('Should not be thrown')}
      [].eachAsync {throw new RuntimeException('Should not be thrown')}
      ''.eachAsync {throw new RuntimeException('Should not be thrown')}
      [].iterator().eachAsync {throw new RuntimeException('Should not be thrown')}
      ''.iterator().eachAsync {throw new RuntimeException('Should not be thrown')}
    }
  }

  public void testEachWithIndexAsync() {
    def result = Collections.synchronizedSet(new HashSet())
    Asynchronizer.withAsynchronizer(5) {ExecutorService service ->
      [1, 2, 3, 4, 5].eachWithIndexAsync {Number number, int index -> result.add(number * index)}
      assertEquals(new HashSet([0, 2, 6, 12, 20]), result)
    }
  }

  public void testEachWithIndexAsyncOnsingleElementCollections() {
    Asynchronizer.withAsynchronizer(5) {ExecutorService service ->
      [1].eachWithIndexAsync {e, i ->}
      [1].eachWithIndexAsync {e, i ->}
      [1].eachWithIndexAsync {e, i ->}
      'a'.eachWithIndexAsync {e, i ->}
      [1].iterator().eachWithIndexAsync {e, i ->}
      'a'.iterator().eachWithIndexAsync {e, i ->}
    }
  }

  public void testEachWithIndexAsyncOnEmpty() {
    Asynchronizer.withAsynchronizer(5) {ExecutorService service ->
      [].eachWithIndexAsync {e, i -> throw new RuntimeException('Should not be thrown')}
      [].eachWithIndexAsync {e, i -> throw new RuntimeException('Should not be thrown')}
      [].eachWithIndexAsync {e, i -> throw new RuntimeException('Should not be thrown')}
      ''.eachWithIndexAsync {e, i -> throw new RuntimeException('Should not be thrown')}
      [].iterator().eachWithIndexAsync {e, i -> throw new RuntimeException('Should not be thrown')}
      ''.iterator().eachWithIndexAsync {e, i -> throw new RuntimeException('Should not be thrown')}
    }
  }

  public void testCollectAsync() {
    Asynchronizer.withAsynchronizer(5) {ExecutorService service ->
      def result = [1, 2, 3, 4, 5].collectAsync {Number number -> number * 10}
      assertEquals(new HashSet([10, 20, 30, 40, 50]), new HashSet((Collection) result))
    }
  }

  public void testCollectAsyncOnRange() {
    Asynchronizer.withAsynchronizer(5) {ExecutorService service ->
      def result = (1..5).collectAsync {Number number -> number * 10}
      assertEquals(new HashSet([10, 20, 30, 40, 50]), new HashSet((Collection) result))
    }
  }

  public void testFindAllAsync() {
    Asynchronizer.withAsynchronizer(5) {ExecutorService service ->
      def result = [1, 2, 3, 4, 5].findAllAsync {Number number -> number > 2}
      assertEquals(new HashSet([3, 4, 5]), new HashSet((Collection) result))
    }
  }

  public void testGrepAsync() {
    Asynchronizer.withAsynchronizer(5) {ExecutorService service ->
      def result = [1, 2, 3, 4, 5].grepAsync(3..6)
      assertEquals(new HashSet([3, 4, 5]), new HashSet((Collection) result))
    }
  }

  public void testFindAsync() {
    Asynchronizer.withAsynchronizer(5) {ExecutorService service ->
      def result = [1, 2, 3, 4, 5].findAsync {Number number -> number > 2}
      assert result in [3, 4, 5]
    }
  }

  public void testAllAsync() {
    Asynchronizer.withAsynchronizer(5) {ExecutorService service ->
      assert [1, 2, 3, 4, 5].allAsync {Number number -> number > 0}
      assert ![1, 2, 3, 4, 5].allAsync {Number number -> number > 2}
    }
  }

  public void testAnyAsync() {
    Asynchronizer.withAsynchronizer(5) {ExecutorService service ->
      assert [1, 2, 3, 4, 5].anyAsync {Number number -> number > 0}
      assert [1, 2, 3, 4, 5].anyAsync {Number number -> number > 2}
      assert ![1, 2, 3, 4, 5].anyAsync {Number number -> number > 6}
    }
  }

  @SuppressWarnings("GroovyOverlyComplexBooleanExpression")
  public void testGroupByAsync() {
    Asynchronizer.withAsynchronizer(5) {ExecutorService service ->
      assert ([1, 2, 3, 4, 5].groupByAsync {Number number -> 1}).size() == 1
      assert ([1, 2, 3, 4, 5].groupByAsync {Number number -> number}).size() == 5
      final def groups = [1, 2, 3, 4, 5].groupByAsync {Number number -> number % 2}
      assert groups.size() == 2
      assert (groups[0].containsAll([2, 4]) && groups[0].size() == 2) || (groups[0].containsAll([1, 3, 5]) && groups[0].size() == 3)
      assert (groups[1].containsAll([2, 4]) && groups[1].size() == 2) || (groups[1].containsAll([1, 3, 5]) && groups[1].size() == 3)
    }
  }

  private def qsort(list) {
   if (!list) return []
   def bucket = list.groupByAsync { it <=> list.first() }
   [ *qsort(bucket[-1]), *bucket[0], *qsort(bucket[1]) ]
  }

  public void testQuicksort() {
    Asynchronizer.withAsynchronizer {
     assertEquals([0, 1, 2, 3],  qsort([0,3,1,2]))
    }
  }

  public void testAsyncTask() {
    Asynchronizer.withAsynchronizer(5) {ExecutorService service ->
      final AtomicBoolean flag = new AtomicBoolean(false)
      final CountDownLatch latch = new CountDownLatch(1)

      service.submit({
        flag.set(true)
        latch.countDown()
      } as Runnable)

      latch.await()
      assert flag.get()
    }
  }
}