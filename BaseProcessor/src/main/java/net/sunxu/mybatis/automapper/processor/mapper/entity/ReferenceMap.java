package net.sunxu.mybatis.automapper.processor.mapper.entity;

import net.sunxu.mybatis.automapper.processor.property.field.ReferenceProperty;

import java.util.*;

class ReferenceMap {

    private Map<ReferenceProperty, Set<ReferenceProperty>> edges = new HashMap<>();

    public boolean hasCycle(ReferenceProperty from, ReferenceProperty to) {
        addEdge(from, to);
        for (Queue<ReferenceProperty> queue = new LinkedList<>();
             to != null && from != to;
             to = queue.poll()) {
            Set<ReferenceProperty> nexts = edges.get(to);
            if (nexts != null) {
                queue.addAll(nexts);
            }
        }
        return from == to;
    }

    private void addEdge(ReferenceProperty from, ReferenceProperty to) {
        Set<ReferenceProperty> edge = edges.get(from);
        if (edge == null) {
            edge = new HashSet<>();
            edges.put(from, edge);
        }
        edge.add(to);
    }

    public void remove(ReferenceProperty node) {
        if(node != null) {
            edges.remove(node);
        }
    }
}
