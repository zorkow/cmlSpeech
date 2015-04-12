/**
 * @file   WalkDepthFirst.java
 * @author Volker Sorge
 *         <a href="mailto:V.Sorge@progressiveaccess.com">Volker Sorge</a>
 * @date   Sun Apr  5 21:13:34 2015
 * 
 * @brief  A helper class for depth first traversal.
 * 
 * 
 */

//

package io.github.egonw.structure;

import io.github.egonw.analysis.RichStructureHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Constructs positions by depth first walk.
 */

public class WalkDepthFirst {

  private class OrderComparator<T> implements Comparator<T> {
    private List<T> order;

    public OrderComparator(List<T> order) {
      this.order = order;
    }

    public int compare(T object1, T object2) {
      Integer index1 = this.order.indexOf(object1);
      Integer index2 = this.order.indexOf(object2);
      return -1 * Integer.compare(index1, index2);
    }
  }

  private ComponentsPositions positions = new ComponentsPositions();
  private List<RichStructure<?>> order = null;
  private RichStructure<?> start = null;

  public WalkDepthFirst(List<RichStructure<?>> order) {
    this.order = order;
    this.start();
    walkDepthFirst();
  }

  public WalkDepthFirst(RichStructure<?> start, List<RichStructure<?>> order) {
    this.start = start;
    this.order = order;
    walkDepthFirst();
  }

  private final void start() {
    if (this.order.isEmpty()) {
      return;
    }
    this.start = order.get(0);
  }

  /**
   * Depth first traversal of structure.
   */
  protected final void walkDepthFirst() {
    OrderComparator<RichStructure<?>> comparator = new OrderComparator<>(
        this.order);
    List<RichStructure<?>> visited = new ArrayList<RichStructure<?>>();
    Stack<RichStructure<?>> frontier = new Stack<RichStructure<?>>();
    frontier.push(this.start);
    while (!frontier.empty()) {
      RichStructure<?> current = frontier.pop();
      if (visited.contains(current)) {
        continue;
      }
      visited.add(current);
      this.positions.addNext(current.getId());
      List<RichStructure<?>> elements = current.getConnections().stream()
          .map(con -> RichStructureHelper.getRichStructure(con.getConnected()))
          .filter(order::contains).collect(Collectors.toList());
      Collections.sort(elements, comparator);
      elements.stream().forEach(frontier::push);
    }
  }

  public final ComponentsPositions getPositions() {
    return this.positions;
  }
}
