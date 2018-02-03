import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.core.graphics.GameGraphics;
import com.alientome.core.vecmath.Vec2;
import com.alientome.game.GameContext;
import com.alientome.game.SpritesLoader;
import com.alientome.game.blocks.Block;
import com.alientome.game.blocks.component.PlatformBlockType;
import com.alientome.game.blocks.component.SlopeBlockType;
import com.alientome.game.collisions.DynamicBoundingBox;
import com.alientome.game.collisions.StaticBoundingBox;
import com.alientome.game.level.LevelMap;
import com.alientome.game.registry.GameRegistry;
import com.alientome.game.registry.Registry;
import com.alientome.impl.blocks.*;
import com.alientome.impl.level.source.CompressedCompoundSource;
import com.alientome.impl.level.source.LevelSource;
import com.alientome.script.ScriptEngine;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.alientome.game.blocks.BlockConstants.WIDTH;
import static java.lang.Math.floor;

public class Scratch {

    static BufferedImage image;

    public static void main(String[] args) throws IOException, InterruptedException {

        /*SpritesLoader.load();

        SpritesLoader.waitUntilLoaded();

        Animation animation = SpritesLoader.animations.get(EntityPlayer.class)[0];

        DataOutputStream os = new DataOutputStream(new FileOutputStream("idle.aam"));

        writeAnimation(animation, os);*/

        /*for (Map.Entry<Class<?>, Animation[]> entry : SpritesLoader.animations.entrySet()) {

            String clsName = entry.getKey().getSimpleName();

            for (int i = 0; i < entry.getValue().length; i++) {

                String fileName = clsName + "_" + i;

                DataOutputStream os = new DataOutputStream(new FileOutputStream(fileName));

                writeAnimation(entry.getValue()[i], os);
            }
        }*/

        AxisAlignedBoundingBox box = new StaticBoundingBox(0, 0, 40, 62);

//        double jumpVelocity = 25;
//        double r = jumpVelocity % 1.5;
//        double q = (jumpVelocity - r) / 1.5;

//        double maxJumpHeight = q * (r + jumpVelocity - 1.5) / 2;
//        System.out.println(maxJumpHeight);
//        System.out.println();

        GameContext context = new GameContext();

        GameRegistry gameRegistry = new GameRegistry();
        Registry<Class<? extends Block>> registry = gameRegistry.getBlocksRegistry();
        registry.set("void", BlockVoid.class);
        registry.set("air", BlockAir.class);
        registry.set("sand", BlockSand.class);
        registry.set("platform_sand", BlockPlatformSand.class);
        registry.set("slope_sand", BlockSlopeSand.class);
        registry.set("spikes", BlockSpikes.class);

        context.setRegistry(gameRegistry);

        SpritesLoader.register("animations.xml");
        SpritesLoader.loadAll();
        SpritesLoader.waitUntilLoaded();

        ScriptEngine engine = new ScriptEngine();
        LevelSource source = new CompressedCompoundSource(context, engine.newParser(), new File("C:/Users/niss3/Desktop/pathfinding test.lvl"));
        source.load();

        LevelMap map = source.getMap();
        int w = map.getWidth();

        GridGraph graph = createGraph(map, box);

        for (WalkablePlatform platform : graph.platforms) {

            List<GraphNode> nodes = platform.nodes;
            for (int i = 0; i < nodes.size() - 1; i++) {
                GraphNode a = nodes.get(i), b = nodes.get(i + 1);
                a.connect(b, 1, ConnectionType.WALK);
                b.connect(a, 1, ConnectionType.WALK);
            }

            GraphNode leftEdge = platform.getLeftEdge();
            if (leftEdge.x > 0)
                fallAlong(map, box, graph, leftEdge, LEFT);

            GraphNode rightEdge = platform.getRightEdge();
            if (rightEdge.x < w - 1)
                fallAlong(map, box, graph, rightEdge, RIGHT);
        }

        image = createImageRepresentation(map, graph);

        Application.launch(App.class);
    }

    private static GridGraph createGraph(LevelMap map, AxisAlignedBoundingBox box) {

        int w = map.getWidth(), h = map.getHeight();

        int[][] grid = new int[w][h];
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid[i].length; j++)
                grid[i][j] = -1;

        List<GraphNode> nodes = new ArrayList<>();
        int nodeIndex = 0;

        List<WalkablePlatform> platforms = new ArrayList<>();
        int platformID = 0;

        Vec2 pos = new Vec2();
        double bh = box.getHeight(), bw = box.getWidth();

        AxisAlignedBoundingBox dynamic = new DynamicBoundingBox(pos, bw, bh);

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h - 1; j++) {
                if (grid[i][j] != -1)
                    continue;
                Block b = map.getBlock(i, j, false);
                if (b.canBeCollidedWith())
                    continue;

                List<GraphNode> platformNodes = new ArrayList<>();

                Block under = map.getBlock(i, j + 1, false);
                while (under.canBeCollidedWith()) {
                    int x = under.blockX, y = under.blockY;
                    if (y == 0)
                        break;

                    b = map.getBlock(x, y - 1, false);
                    if (b.canBeCollidedWith()) {
                        if (isSlope(b)) {
                            under = b;
                            continue;
                        } else if (!isPlatform(b))
                            break;
                    }

                    boolean free;
                    pos.set(x * WIDTH, y * WIDTH - bh);
                    free = testCollision(map, dynamic);
                    if (!free) {
                        pos.addX(WIDTH - bw);
                        free = testCollision(map, dynamic);
                    }

                    if (free) {
                        GraphNode node = new GraphNode(x, y - 1, platformID);
                        grid[x][y - 1] = nodeIndex++;
                        nodes.add(node);
                        platformNodes.add(node);

                        if (x == w - 1)
                            break;
                        else {
                            boolean wasSlope = isSlope(under);
                            under = map.getBlock(x + 1, y, false);

                            if (wasSlope && !under.canBeCollidedWith())
                                under = map.getBlock(x + 1, y + 1, false);
                        }
                    } else break;
                }

                if (platformNodes.size() > 0) {
                    platforms.add(new WalkablePlatform(platformNodes));
                    platformID++;
                }
            }
        }

        return new GridGraph(grid, nodes, platforms);
    }

    private static BufferedImage createImageRepresentation(LevelMap map, GridGraph graph) {

        int w = map.getWidth(), h = map.getHeight();

        BufferedImage image = new BufferedImage(w * WIDTH, h * WIDTH, BufferedImage.TYPE_INT_ARGB);

        Graphics g = image.getGraphics();
        g.setFont(g.getFont().deriveFont(10f));

        GameGraphics graphics = new GameGraphics(g, new Point(), 0, 0);

        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                map.getBlock(i, j, false).draw(graphics, false);
                g.drawString(i + "-" + j, i * WIDTH, j * WIDTH + 10);
                if (graph.grid[i][j] != -1) {
                    g.setColor(new Color(0, 127, 127, 127));
                    g.fillRect(i * WIDTH, j * WIDTH, WIDTH, WIDTH);
                }
            }
        }

        graph.forEachNode(node -> node.drawConnections(g));

        return image;
    }

    private static final int LEFT = 0, RIGHT = 1;

    private static void fallAlong(LevelMap map, AxisAlignedBoundingBox box, GridGraph graph, GraphNode from, int dir) {

        double bh = box.getHeight(), bw = box.getWidth();
        int startX;
        int startY = (int) ((from.y + 1) * WIDTH - bh);

        switch (dir) {
            case LEFT:
                startX = (int) (from.x * WIDTH - bw);
                break;

            case RIGHT:
                startX = (from.x + 1) * WIDTH;
                break;

            default:
                throw new IllegalArgumentException("Direction " + dir);
        }

        Vec2 pos = new Vec2(startX, startY);

        AxisAlignedBoundingBox dynamic = new DynamicBoundingBox(pos, bw, bh);

        int h = map.getHeight();

        int minBX = (int) floor(dynamic.getMinX() / WIDTH);
        int maxBX = (int) floor(dynamic.getMaxX() / WIDTH);

        int[] start = {maxBX, minBX};
        int[] inc = {-1, 1};

        for (int i = from.y; i < h; i++) {
            if (!testCollision(map, dynamic))
                break;
            pos.add(0, 1);

            for (int j = start[dir]; j >= minBX && j <= maxBX; j += inc[dir]) {
                Block b = map.getBlock(j, i + 1, false);
                if (b.canBeCollidedWith() && b.getBoundingBox().intersects(dynamic)) {
                    GraphNode other = graph.get(j, i);
                    if (other != null) {
                        if (i == from.y)
                            from.connect(other, 1, ConnectionType.WALK);
                        else
                            from.connect(other, 2, ConnectionType.FALL);
                    }
                    return;
                }
            }
            pos.add(0, WIDTH - 1);
        }
    }

    private static boolean isPlatform(Block b) {
        return b.type instanceof PlatformBlockType;
    }

    private static boolean isSlope(Block b) {
        return b.type instanceof SlopeBlockType;
    }

    private static boolean testCollision(LevelMap map, AxisAlignedBoundingBox box) {

        int minBX = (int) floor(box.getMinX() / WIDTH);
        int minBY = (int) floor(box.getMinY() / WIDTH);
        int maxBX = (int) floor(box.getMaxX() / WIDTH);
        int maxBY = (int) floor(box.getMaxY() / WIDTH);

        for (int i = minBX; i <= maxBX; i++)
            for (int j = minBY; j <= maxBY; j++) {
                Block b = map.getBlock(i, j);
                if (b.canBeCollidedWith() && !isPlatform(b) && b.getBoundingBox().intersects(box))
                    return false;
            }

        return true;
    }

    private static class WalkablePlatform {

        private final List<GraphNode> nodes;

        public WalkablePlatform(List<GraphNode> nodes) {
            this.nodes = Collections.unmodifiableList(nodes);
        }

        public GraphNode getLeftEdge() {
            return nodes.get(0);
        }

        public GraphNode getRightEdge() {
            return nodes.get(nodes.size() - 1);
        }
    }

    private static class GridGraph {

        private final int[][] grid;
        private final List<GraphNode> nodes;
        private final List<WalkablePlatform> platforms;

        public GridGraph(int[][] grid, List<GraphNode> nodes, List<WalkablePlatform> platforms) {
            this.grid = grid;
            this.nodes = nodes;
            this.platforms = platforms;
        }

        public void forEachNode(Consumer<GraphNode> action) {
            nodes.forEach(action);
        }

        public GraphNode get(int x, int y) {
            int index = grid[x][y];
            if (index == -1)
                return null;
            return nodes.get(index);
        }
    }

    private static class GraphNode {

        private static final double ARROW_ANGLE = Math.toRadians(30);
        private static final double ARROW_COS = Math.cos(ARROW_ANGLE);
        private static final double ARROW_SIN = Math.sin(ARROW_ANGLE);
        private static final double ARROW_LENGTH = 5;

        private final int x, y;
        private final List<GraphConnection> connections = new ArrayList<>();
        private final int platformID;

        public GraphNode(int x, int y, int platformID) {
            this.x = x;
            this.y = y;
            this.platformID = platformID;
        }

        public void connect(GraphNode to, int cost, ConnectionType type) {

            Objects.requireNonNull(to);

            connections.add(new GraphConnection(this, to, cost, type));
        }

        public void drawConnections(Graphics g) {

            int x1 = x * WIDTH + WIDTH / 2;
            int y1 = y * WIDTH + WIDTH / 2;

            for (GraphConnection connection : connections) {
                g.setColor(connection.type.color);

                int x2 = connection.to.x * WIDTH + WIDTH / 2;
                int y2 = connection.to.y * WIDTH + WIDTH / 2;
                g.drawLine(x1, y1, x2, y2);

                double a = x2 - x1;
                double b = y2 - y1;

                double lineLength = Math.sqrt(a * a + b * b);

                double lineCos = a / lineLength;
                double lineSin = b / lineLength;

                //1st arrow line
                double cos1 = -(lineCos * ARROW_COS + lineSin * ARROW_SIN);
                double sin1 = lineCos * ARROW_SIN - lineSin * ARROW_COS;

                int dx1 = (int) (ARROW_LENGTH * cos1);
                int dy1 = (int) (ARROW_LENGTH * sin1);

                //2nd arrow line
                double cos2 = lineSin * ARROW_SIN - lineCos * ARROW_COS;
                double sin2 = -(lineSin * ARROW_COS + lineCos * ARROW_SIN);

                int dx2 = (int) (ARROW_LENGTH * cos2);
                int dy2 = (int) (ARROW_LENGTH * sin2);

                g.drawLine(x2, y2, x2 + dx1, y2 + dy1);
                g.drawLine(x2, y2, x2 + dx2, y2 + dy2);
            }
        }
    }

    private static class GraphConnection {

        private final GraphNode from;
        private final GraphNode to;
        private final int cost;
        private final ConnectionType type;

        private GraphConnection(GraphNode from, GraphNode to, int cost, ConnectionType type) {
            this.from = from;
            this.to = to;
            this.cost = cost;
            this.type = type;
        }
    }

    private enum ConnectionType {

        WALK(new Color(0, 127, 0)),
        JUMP(new Color(127, 0, 0)),
        FALL(new Color(0, 0, 127));

        private final Color color;

        ConnectionType(Color color) {
            this.color = color;
        }
    }

    public static class App extends Application {

        @Override
        public void start(Stage stage) {
            ImageView view = new ImageView(SwingFXUtils.toFXImage(image, null));
            Pane root = new Pane(view);

            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.show();
        }
    }
}