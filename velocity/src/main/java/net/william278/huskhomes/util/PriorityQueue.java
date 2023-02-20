package net.william278.huskhomes.util;

public final class PriorityQueue<E> extends java.util.PriorityQueue<E> {

    public int indexOf(final Object o) {
        if (o != null) {
            final var es = this.toArray();
            for (var i = 0; i < this.size(); i++)
                if (o.equals(es[i])) {
                    return i;
                }
        }
        return -1;
    }
}
