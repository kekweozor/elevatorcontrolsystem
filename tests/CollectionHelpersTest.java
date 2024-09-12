package sysc3303_elevator.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import sysc3303_elevator.CollectionHelpers;

public class CollectionHelpersTest {

    @Test
    public void testSplitList() throws InterruptedException {
        var input = new ArrayList<Integer>();
        input.add(0);
        input.add(1);
        input.add(2);
        input.add(0);
        input.add(65);
        input.add(3);
        input.add(6);

        var results = CollectionHelpers.splitBy(input, elem -> (elem & 1) == 0);

        assertArrayEquals(new Integer[] {
            0, 2, 0, 6
        }, results.first().toArray());
        assertArrayEquals(new Integer[] {
            1, 65, 3
        }, results.second().toArray());
    }
}
