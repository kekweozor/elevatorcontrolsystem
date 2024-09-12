package sysc3303_elevator;

import java.util.ArrayList;
import java.util.Collection;

public class CollectionHelpers {
    public static interface SplitFun<T> {
        boolean splitFun(T item);
    }

    public static <T> Pair<ArrayList<T>, ArrayList<T>> splitBy(Collection<T> collection, SplitFun<T> fun) {
        var trueList = new ArrayList<T>();
        var falseList = new ArrayList<T>();
        for (var item : collection) {
            if (fun.splitFun(item)) {
                trueList.add(item);
            } else {
                falseList.add(item);
            }
        }
        return new Pair<ArrayList<T>, ArrayList<T>>(trueList, falseList);
    }
}