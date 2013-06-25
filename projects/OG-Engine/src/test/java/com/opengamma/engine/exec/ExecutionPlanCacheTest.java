/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.engine.exec;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.Future;

import net.sf.ehcache.CacheManager;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.ComputationTargetSpecification;
import com.opengamma.engine.depgraph.DependencyGraph;
import com.opengamma.engine.depgraph.DependencyNode;
import com.opengamma.engine.exec.ExecutionPlan;
import com.opengamma.engine.exec.ExecutionPlanCache;
import com.opengamma.engine.exec.GraphFragmentContext;
import com.opengamma.engine.exec.ExecutionPlanCache.DependencyGraphKey;
import com.opengamma.engine.exec.ExecutionPlanCache.DependencyNodeKey;
import com.opengamma.engine.exec.stats.GraphExecutorStatisticsGatherer;
import com.opengamma.engine.target.ComputationTargetType;
import com.opengamma.engine.test.MockFunction;
import com.opengamma.engine.value.ValueProperties;
import com.opengamma.engine.value.ValuePropertyNames;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.id.UniqueId;
import com.opengamma.util.ehcache.EHCacheUtils;
import com.opengamma.util.test.TestGroup;

/**
 * Test.
 */
@Test(groups = {TestGroup.UNIT, "ehcache"})
public class ExecutionPlanCacheTest {

  private CacheManager _cacheManager;

  @BeforeClass
  public void setUpClass() {
    _cacheManager = EHCacheUtils.createTestCacheManager(getClass());
  }

  @AfterClass
  public void tearDownClass() {
    EHCacheUtils.shutdownQuiet(_cacheManager);
  }

  @BeforeMethod
  public void setUp() {
    EHCacheUtils.clear(_cacheManager);
  }

  //-------------------------------------------------------------------------
  public void testDependencyNodeKey_same() {
    final DependencyNode a = new DependencyNode(new ComputationTarget(ComputationTargetType.PRIMITIVE, UniqueId.of("Test", "A")));
    a.setFunction(new MockFunction("Foo", new ComputationTarget(ComputationTargetType.PRIMITIVE, UniqueId.of("Test", "A"))));
    a.addInputValue(new ValueSpecification("1", ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Bar").get()));
    a.addInputValue(new ValueSpecification("2", ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Bar").get()));
    a.addOutputValue(new ValueSpecification("1", ComputationTargetSpecification.of(UniqueId.of("Test", "A")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Foo").get()));
    a.addOutputValue(new ValueSpecification("2", ComputationTargetSpecification.of(UniqueId.of("Test", "A")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Foo").get()));
    final DependencyNode b = new DependencyNode(new ComputationTarget(ComputationTargetType.PRIMITIVE, UniqueId.of("Test", "A")));
    b.setFunction(new MockFunction("Foo", new ComputationTarget(ComputationTargetType.PRIMITIVE, UniqueId.of("Test", "A"))));
    b.addInputValue(new ValueSpecification("1", ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Bar").get()));
    b.addInputValue(new ValueSpecification("2", ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Bar").get()));
    b.addOutputValue(new ValueSpecification("1", ComputationTargetSpecification.of(UniqueId.of("Test", "A")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Foo").get()));
    b.addOutputValue(new ValueSpecification("2", ComputationTargetSpecification.of(UniqueId.of("Test", "A")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Foo").get()));
    // Note: if this test fails because the next two both become true then the DependencyNodeKey class could be redundant
    assertFalse(a.equals(b));
    assertFalse(b.equals(a));
    final DependencyNodeKey ak = new DependencyNodeKey(a);
    final DependencyNodeKey bk = new DependencyNodeKey(b);
    assertTrue(ak.equals(bk));
    assertTrue(bk.equals(ak));
    assertEquals(ak.hashCode(), bk.hashCode());
  }

  public void testDependencyNodeKey_target() {
    final DependencyNode a = new DependencyNode(new ComputationTarget(ComputationTargetType.PRIMITIVE, UniqueId.of("Test", "A")));
    a.setFunction(new MockFunction("Foo", new ComputationTarget(ComputationTargetType.PRIMITIVE, UniqueId.of("Test", "A"))));
    a.addInputValue(new ValueSpecification("1", ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Bar").get()));
    a.addInputValue(new ValueSpecification("2", ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Bar").get()));
    final DependencyNode b = new DependencyNode(new ComputationTarget(ComputationTargetType.PRIMITIVE, UniqueId.of("Test", "B")));
    b.setFunction(new MockFunction("Foo", new ComputationTarget(ComputationTargetType.PRIMITIVE, UniqueId.of("Test", "B"))));
    b.addInputValue(new ValueSpecification("1", ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Bar").get()));
    b.addInputValue(new ValueSpecification("2", ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Bar").get()));
    final DependencyNodeKey ak = new DependencyNodeKey(a);
    final DependencyNodeKey bk = new DependencyNodeKey(b);
    assertFalse(ak.equals(bk));
    assertFalse(bk.equals(ak));
  }

  public void testDependencyNodeKey_function() {
    final DependencyNode a = new DependencyNode(new ComputationTarget(ComputationTargetType.PRIMITIVE, UniqueId.of("Test", "A")));
    a.setFunction(new MockFunction("Foo", new ComputationTarget(ComputationTargetType.PRIMITIVE, UniqueId.of("Test", "A"))));
    a.addInputValue(new ValueSpecification("1", ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Bar").get()));
    a.addInputValue(new ValueSpecification("2", ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Bar").get()));
    final DependencyNode b = new DependencyNode(new ComputationTarget(ComputationTargetType.PRIMITIVE, UniqueId.of("Test", "A")));
    b.setFunction(new MockFunction("Bar", new ComputationTarget(ComputationTargetType.PRIMITIVE, UniqueId.of("Test", "A"))));
    b.addInputValue(new ValueSpecification("1", ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Bar").get()));
    b.addInputValue(new ValueSpecification("2", ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Bar").get()));
    final DependencyNodeKey ak = new DependencyNodeKey(a);
    final DependencyNodeKey bk = new DependencyNodeKey(b);
    assertFalse(ak.equals(bk));
    assertFalse(bk.equals(ak));
  }

  public void testDependencyNodeKey_inputs() {
    final DependencyNode a = new DependencyNode(new ComputationTarget(ComputationTargetType.PRIMITIVE, UniqueId.of("Test", "A")));
    a.setFunction(new MockFunction("Foo", new ComputationTarget(ComputationTargetType.PRIMITIVE, UniqueId.of("Test", "A"))));
    a.addInputValue(new ValueSpecification("1", ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Bar").get()));
    a.addOutputValue(new ValueSpecification("1", ComputationTargetSpecification.of(UniqueId.of("Test", "A")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Foo").get()));
    a.addOutputValue(new ValueSpecification("2", ComputationTargetSpecification.of(UniqueId.of("Test", "A")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Foo").get()));
    final DependencyNode b = new DependencyNode(new ComputationTarget(ComputationTargetType.PRIMITIVE, UniqueId.of("Test", "A")));
    b.setFunction(new MockFunction("Foo", new ComputationTarget(ComputationTargetType.PRIMITIVE, UniqueId.of("Test", "A"))));
    b.addInputValue(new ValueSpecification("1", ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Bar").get()));
    b.addInputValue(new ValueSpecification("2", ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Bar").get()));
    b.addOutputValue(new ValueSpecification("1", ComputationTargetSpecification.of(UniqueId.of("Test", "A")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Foo").get()));
    b.addOutputValue(new ValueSpecification("2", ComputationTargetSpecification.of(UniqueId.of("Test", "A")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Foo").get()));
    final DependencyNodeKey ak = new DependencyNodeKey(a);
    final DependencyNodeKey bk = new DependencyNodeKey(b);
    assertFalse(ak.equals(bk));
    assertFalse(bk.equals(ak));
  }

  public void testDependencyNodeKey_outputs() {
    final DependencyNode a = new DependencyNode(new ComputationTarget(ComputationTargetType.PRIMITIVE, UniqueId.of("Test", "A")));
    a.setFunction(new MockFunction("Foo", new ComputationTarget(ComputationTargetType.PRIMITIVE, UniqueId.of("Test", "A"))));
    a.addInputValue(new ValueSpecification("1", ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Bar").get()));
    a.addInputValue(new ValueSpecification("2", ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Bar").get()));
    a.addOutputValue(new ValueSpecification("1", ComputationTargetSpecification.of(UniqueId.of("Test", "A")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Foo").get()));
    a.addOutputValue(new ValueSpecification("2", ComputationTargetSpecification.of(UniqueId.of("Test", "A")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Foo").get()));
    final DependencyNode b = new DependencyNode(new ComputationTarget(ComputationTargetType.PRIMITIVE, UniqueId.of("Test", "A")));
    b.setFunction(new MockFunction("Foo", new ComputationTarget(ComputationTargetType.PRIMITIVE, UniqueId.of("Test", "A"))));
    b.addInputValue(new ValueSpecification("1", ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Bar").get()));
    b.addInputValue(new ValueSpecification("2", ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Bar").get()));
    b.addOutputValue(new ValueSpecification("1", ComputationTargetSpecification.of(UniqueId.of("Test", "A")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Foo").get()));
    final DependencyNodeKey ak = new DependencyNodeKey(a);
    final DependencyNodeKey bk = new DependencyNodeKey(b);
    assertFalse(ak.equals(bk));
    assertFalse(bk.equals(ak));
  }

  private DependencyGraph createDependencyGraph() {
    final DependencyGraph graph = new DependencyGraph("Default");
    final DependencyNode[] nodes = new DependencyNode[10];
    for (int i = 0; i < nodes.length; i++) {
      final DependencyNode node = new DependencyNode(new ComputationTarget(ComputationTargetType.PRIMITIVE, UniqueId.of("Test", "X")));
      node.setFunction(new MockFunction("Foo" + i, new ComputationTarget(ComputationTargetType.PRIMITIVE, UniqueId.of("Test", "X"))));
      node.addOutputValue(new ValueSpecification(Integer.toString(i), ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Foo" + i).get()));
      if (i > 0) {
        node.addInputNode(nodes[i - 1]);
        node.addInputValue(new ValueSpecification(Integer.toString(i - 1), ComputationTargetSpecification.of(UniqueId.of("Test", "X")),
            ValueProperties.with(ValuePropertyNames.FUNCTION, "Foo" + (i - 1)).get()));
      }
      graph.addDependencyNode(node);
      nodes[i] = node;
    }
    graph.addTerminalOutput(new ValueRequirement("0", ComputationTargetSpecification.of(UniqueId.of("Test", "X"))),
        new ValueSpecification("0", ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Foo0").get()));
    return graph;
  }

  public void testDependencyGraphKey_same() {
    final DependencyGraph a = createDependencyGraph();
    final DependencyGraph b = createDependencyGraph();
    // Note: if this test fails because the next two both become true then the DependencyGraphKey class could be redundant
    assertFalse(a.equals(b));
    assertFalse(b.equals(a));
    final DependencyGraphKey ak = new DependencyGraphKey(a, 0);
    final DependencyGraphKey bk = new DependencyGraphKey(b, 0);
    assertTrue(ak.equals(bk));
    assertTrue(bk.equals(ak));
    assertEquals(ak.hashCode(), bk.hashCode());
  }

  public void testDependencyGraphKey_initId() {
    final DependencyGraph a = createDependencyGraph();
    final DependencyGraph b = createDependencyGraph();
    final DependencyGraphKey ak = new DependencyGraphKey(a, 1);
    final DependencyGraphKey bk = new DependencyGraphKey(b, 2);
    assertFalse(ak.equals(bk));
    assertFalse(bk.equals(ak));
  }

  public void testDependencyGraphKey_terminals() {
    final DependencyGraph a = createDependencyGraph();
    a.addTerminalOutput(new ValueRequirement("1", ComputationTargetSpecification.of(UniqueId.of("Test", "X"))), new ValueSpecification("1",
        ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Foo1").get()));
    final DependencyGraph b = createDependencyGraph();
    b.addTerminalOutput(new ValueRequirement("2", ComputationTargetSpecification.of(UniqueId.of("Test", "X"))), new ValueSpecification("2",
        ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Foo2").get()));
    final DependencyGraphKey ak = new DependencyGraphKey(a, 0);
    final DependencyGraphKey bk = new DependencyGraphKey(b, 0);
    assertFalse(ak.equals(bk));
    assertFalse(bk.equals(ak));
  }

  public void testDependencyGraphKey_node() {
    final DependencyGraph a = createDependencyGraph();
    final DependencyNode n = new DependencyNode(new ComputationTarget(ComputationTargetType.PRIMITIVE, UniqueId.of("Test", "A")));
    n.setFunction(new MockFunction("Foo", new ComputationTarget(ComputationTargetType.PRIMITIVE, UniqueId.of("Test", "A"))));
    n.addInputValue(new ValueSpecification("1", ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Bar").get()));
    n.addInputValue(new ValueSpecification("2", ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Bar").get()));
    n.addOutputValue(new ValueSpecification("1", ComputationTargetSpecification.of(UniqueId.of("Test", "A")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Foo").get()));
    n.addOutputValue(new ValueSpecification("2", ComputationTargetSpecification.of(UniqueId.of("Test", "A")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Foo").get()));
    a.addDependencyNode(n);
    final DependencyGraph b = createDependencyGraph();
    final DependencyGraphKey ak = new DependencyGraphKey(a, 0);
    final DependencyGraphKey bk = new DependencyGraphKey(b, 0);
    assertFalse(ak.equals(bk));
    assertFalse(bk.equals(ak));
  }

  public void testDependencyGraphKey_serialization() throws Exception {
    final DependencyGraphKey a = new DependencyGraphKey(createDependencyGraph(), 0);
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(a);
    final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
    final DependencyGraphKey b = (DependencyGraphKey) ois.readObject();
    assertEquals(b, a);
  }

  private ExecutionPlan createExecutionPlan() {
    return new ExecutionPlan() {

      private static final long serialVersionUID = 1L;

      @Override
      public Future<DependencyGraph> run(final GraphFragmentContext context, final GraphExecutorStatisticsGatherer statistics) {
        return null;
      }

      @Override
      public ExecutionPlan withNodes(final Map<DependencyNodeKey, DependencyNode> nodes) {
        return this;
      }

      @Override
      /* package */boolean isTestEqual(final ExecutionPlan plan) {
        return plan == this;
      }

    };
  }

  //-------------------------------------------------------------------------
  public void testCache_identity() {
    final ExecutionPlanCache cache = new ExecutionPlanCache(_cacheManager);
    final DependencyGraph graph = createDependencyGraph();
    final ExecutionPlan plan = createExecutionPlan();
    cache.cachePlan(graph, 0, plan);
    // Change the graph object so the key approach can't work. This is done as an optimization to avoid the cost of building
    // the "key" objects. If the behavior of the view processor changes and it starts modifying graphs between cycles then
    // a problem will occur as the previous execution plan will be used.
    graph.addTerminalOutput(new ValueRequirement("1", ComputationTargetSpecification.of(UniqueId.of("Test", "X"))),
        new ValueSpecification("1", ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Foo1").get()));
    final ExecutionPlan cached = cache.getCachedPlan(graph, 0);
    assertEquals(cached, plan);
    cache.shutdown();
  }

  public void testCache_key() {
    final ExecutionPlanCache cache = new ExecutionPlanCache(_cacheManager);
    final ExecutionPlan plan = createExecutionPlan();
    cache.cachePlan(createDependencyGraph(), 0, plan);
    final ExecutionPlan cached = cache.getCachedPlan(createDependencyGraph(), 0);
    assertEquals(cached, plan);
    cache.shutdown();
  }

  public void testCache_identity_invalid() {
    final ExecutionPlanCache cache = new ExecutionPlanCache(_cacheManager);
    final DependencyGraph graph = createDependencyGraph();
    final ExecutionPlan plan = createExecutionPlan();
    cache.cachePlan(graph, 0, plan);
    graph.addTerminalOutput(new ValueRequirement("1", ComputationTargetSpecification.of(UniqueId.of("Test", "X"))),
        new ValueSpecification("1", ComputationTargetSpecification.of(UniqueId.of("Test", "X")), ValueProperties.with(ValuePropertyNames.FUNCTION, "Foo1").get()));
    final ExecutionPlan cached = cache.getCachedPlan(graph, 1);
    assertNull(cached);
    cache.shutdown();
  }

}