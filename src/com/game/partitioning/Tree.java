package com.game.partitioning;

import com.game.GameObject;
import com.game.blocks.Block;
import com.util.Vec2;

import java.util.ArrayList;
import java.util.Arrays;

import static com.util.Util.log;

/**
 * Holds a 2d array of <code>Cell</code>s dividing the <code>Level</code>
 * into smaller parts for collision detection of <code>GameObjects</code>.
 */
public class Tree {

    static int updated = 0;
    private final Cell[][] cells;

    /**
     * Constructs an empty <code>Tree</code>.
     *
     * @param x the number of horizontal cells
     * @param y the number of vertical cells
     */
    public Tree(int x, int y) {

        log("Initializing cells", 0);

        long time = System.currentTimeMillis();

        cells = new Cell[x][y];

        for (int i = 0; i < cells.length; i++)
            for (int j = 0; j < cells[i].length; j++)
                cells[i][j] = new Cell(i, j);

        log("Initialized " + x * y + " cells in " + (System.currentTimeMillis() - time) + " ms", 0);
    }

    /**
     * Private method to avoid bound-checking.
     *
     * @param x the x coordinate of the <code>Cell</code>
     * @param y the y coordinate of the <code>Cell</code>
     * @return the <code>Cell</code> at specified coordinates, or <code>null</code> if out of bounds
     */
    private Cell get(int x, int y) {

        try {
            return cells[x][y];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Adds the given object to this <code>Tree</code>. If the object cannot be placed in an existing
     * <code>Cell</code> (which shouldn't happen under normal circumstances), it is ignored, else it
     * is added to between 1 and 4 <code>Cell</code>s, inclusive, in which it at least partially fits.
     *
     * @param object the <code>GameObject</code> to add
     */
    public void add(GameObject object) {

        int x = (int) (object.getPos().x / Block.width);
        int y = (int) (object.getPos().y / Block.width);

        for (int i = x; i <= x + 1; i++) {

            for (int j = y; j <= y + 1; j++) {

                Cell cell = get(i, j);

                if (cell != null && cell.canContain(object)) {

                    cell.add(object);
                }

            }
        }
    }

    /**
     * Removes the given object from this <code>Tree</code>. If no <code>Cell</code>s contain this
     * object, it is ignored.
     *
     * @param object the <code>GameObject</code> to remove
     */
    public void remove(GameObject object) {

        int x = (int) (object.getPos().x / Block.width);
        int y = (int) (object.getPos().y / Block.width);

        removeObjectFromSquare(object, x, y);
    }

    /**
     * Moves the given object in the tree according to the given previous position and its current position.
     *
     * @param prevPos the previous position vector
     * @param object  the <code>GameObject</code> to move
     */
    public void move(Vec2 prevPos, GameObject object) {

        int oldX = (int) (prevPos.x / Block.width);
        int oldY = (int) (prevPos.y / Block.width);

        removeObjectFromSquare(object, oldX, oldY);

        add(object);
    }

    private void removeObjectFromSquare(GameObject object, int startX, int startY) {
        for (int i = startX; i <= startX + 1; i++) {

            for (int j = startY; j <= startY + 1; j++) {

                Cell cell = get(i, j);

                if (cell != null) cell.remove(object);
            }
        }
    }

    /**
     * Empties this <code>Tree</code>, that is empty all of its <code>Cell</code>s.
     *
     * @see Cell#empty()
     */
    public void clear() {

        for (Cell[] cells : this.cells) for (Cell cell : cells) cell.empty();
    }

    /**
     * Updates all non-empty <code>Cell</code>s in this <code>Tree</code>.
     *
     * @return the number of updated <code>Cell</code>s
     * @see Cell#update()
     */
    public int updateCells() {

        updated = 0;

        Arrays.stream(cells).flatMap(Arrays::stream)
                .filter(Cell::hasObjects)
                .forEach(Cell::update);

        return updated;
    }

    /**
     * Creates and returns an <code>ArrayList</code> of <code>GameObjects</code>s answering
     * the following constraints : they must be an instance of type, and their distance to
     * the point (x;y) must be inferior to range. Optional excluded objects can be provided.
     *
     * @param type     the type of <code>GameObjects</code> to get
     * @param x        the center x for range checking
     * @param y        the center y for range checking
     * @param range    the maximum range
     * @param excluded the <code>GameObject</code>s to ignore
     * @return an <code>ArrayList</code> containing all found <code>GameObjects</code>
     */
    public ArrayList<GameObject> getObjectsInRange(Class<? extends GameObject> type, double x, double y, double range, GameObject... excluded) {

        ArrayList<GameObject> objects = new ArrayList<>();

        int centerX = (int) (x / Block.width);
        int centerY = (int) (y / Block.width);
        int cellRange = (int) (range / Block.width);

        double rangeSq = range * range;

        for (int i = centerX - cellRange; i < centerX + cellRange; i++) {

            for (int j = centerY - cellRange; j < centerY + cellRange; j++) {

                Cell cell = get(i, j);

                if (cell != null && cell.hasObjects()) {

                    cell.objects
                            .stream()
                            .filter(object ->
                                    type.isInstance(object) &&
                                            !objects.contains(object) &&
                                            object.getPos().distanceSq(x, y) <= rangeSq)
                            .forEach(objects::add);
                }
            }
        }

        objects.removeAll(Arrays.asList(excluded));

        return objects;
    }

    /**
     * Returns the number of <code>GameObject</code>s answering the following constraints :
     * they must be an instance of type, and their distance to the point (x;y) must be
     * inferior to range. Optional excluded objects can be provided.
     *
     * @param type     the type of <code>GameObjects</code> to count
     * @param x        the center x for range checking
     * @param y        the center y for range checking
     * @param range    the maximum range
     * @param excluded the <code>GameObject</code>s to ignore
     * @return the count of found <code>GameObjects</code>
     */
    public int countObjectsInRange(Class<? extends GameObject> type, double x, double y, double range, GameObject... excluded) {

        //Most efficient method atm in order not to count the same object more than once.
        return getObjectsInRange(type, x, y, range, excluded).size();
    }
}
