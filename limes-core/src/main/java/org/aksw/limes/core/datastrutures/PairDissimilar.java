package org.aksw.limes.core.datastrutures;


/**
 * This generic class combines pair of data  from different datatypes
 *
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */
public class PairDissimilar<S,T> {
    public final S a;
    public final T b;

    public PairDissimilar(S a, T b) {
        super();
        this.a = a;
        this.b = b;
    }

    public String toString() {
        return a + " - " + b;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((a == null) ? 0 : a.hashCode());
        result = prime * result + ((b == null) ? 0 : b.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PairSimilar<?> other = (PairSimilar<?>) obj;
        if (a == null) {
            if (other.a != null)
                return false;
        } else if (!a.equals(other.a))
            return false;
        if (b == null) {
            if (other.b != null)
                return false;
        } else if (!b.equals(other.b))
            return false;
        return true;
    }


}
