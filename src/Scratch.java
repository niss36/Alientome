import com.alientome.core.SharedInstances;
import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.core.graphics.GameGraphics;
import com.alientome.core.util.Vec2;
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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.alientome.core.SharedNames.REGISTRY;
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

        double jumpVelocity = 25;
        double r = jumpVelocity % 1.5;
        double q = (jumpVelocity - r) / 1.5;

        double maxJumpHeight = q * (r + jumpVelocity - 1.5) / 2;
        System.out.println(maxJumpHeight);
        System.out.println();

        GameRegistry gameRegistry = new GameRegistry();
        Registry<Class<? extends Block>> registry = gameRegistry.getBlocksRegistry();
        registry.set("void", BlockVoid.class);
        registry.set("air", BlockAir.class);
        registry.set("sand", BlockSand.class);
        registry.set("platform_sand", BlockPlatformSand.class);
        registry.set("slope_sand", BlockSlopeSand.class);
        registry.set("spikes", BlockSpikes.class);
        SharedInstances.set(REGISTRY, gameRegistry);

        SpritesLoader.register("animations.xml");
        SpritesLoader.loadAll();
        SpritesLoader.waitUntilLoaded();

        ScriptEngine engine = new ScriptEngine();
        LevelSource source = new CompressedCompoundSource(engine.newParser(), new File("C:/Users/niss3/Desktop/pathfinding test.lvl"));
        source.load();

        LevelMap map = source.getMap();
        int w = map.getWidth(), h = map.getHeight();

        boolean[][] bitmap = new boolean[w][h];

        List<Platform> platforms = createPlatforms(map, box, bitmap);

        GridGraph graph = new GridGraph(bitmap);

        image = new BufferedImage(w * WIDTH, h * WIDTH, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();

        GameGraphics graphics = new GameGraphics(g, new Point(), 0, 0);

        StringBuilder sb = new StringBuilder();

        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                map.getBlock(i, j, false).draw(graphics, false);
                if (bitmap[i][j]) {
                    g.setColor(new Color(0, 127, 127, 127));
                    g.fillRect(i * WIDTH, j * WIDTH, WIDTH, WIDTH);
                }
                if (map.getBlock(i, j, false).canBeCollidedWith())
                    sb.append('B');
                else if (bitmap[i][j])
                    sb.append('#');
                else
                    sb.append(' ');
            }
            sb.append('\n');
        }

        for (Platform platform : platforms) {
            List<Point> points = platform.points;
            for (int i = 0; i < points.size() - 1; i++) {
                Point a = points.get(i), b = points.get(i + 1);
                GraphNode nodeA = graph.get(a.x, a.y), nodeB = graph.get(b.x, b.y);
                nodeA.connect(nodeB, 1, ConnectionType.WALK);
                nodeB.connect(nodeA, 1, ConnectionType.WALK);
            }

            Point right = points.get(points.size() - 1);
            GraphNode rightEdge = graph.get(right.x, right.y);
            if (right.x < w - 1)
                for (int i = right.y + 1; i < h; i++) {
                    if (map.getBlock(right.x + 1, i, false).canBeCollidedWith())
                        break;
                    GraphNode other = graph.get(right.x + 1, i);
                    if (other != null)
                        rightEdge.connect(other, 2, ConnectionType.FALL);
                }

            Point left = points.get(0);
            GraphNode leftEdge = graph.get(left.x, left.y);
            if (left.x > 0)
                for (int i = left.y + 1; i < h; i++) {
                    if (map.getBlock(left.x - 1, i, false).canBeCollidedWith())
                        break;
                    GraphNode other = graph.get(left.x - 1, i);
                    if (other != null)
                        leftEdge.connect(other, 2, ConnectionType.FALL);
                }
        }

        graph.forEachNode(node -> node.drawConnections(g));

        System.out.println(sb);

        Application.launch(App.class);
    }

    private static List<Platform> createPlatforms(LevelMap map, AxisAlignedBoundingBox box, boolean[][] visited) {

        List<Platform> platforms = new ArrayList<>();

        Vec2 pos = new Vec2();
        double bh = box.getHeight(), bw = box.getWidth();

        AxisAlignedBoundingBox dynamic = new DynamicBoundingBox(pos, bw, bh);

        int w = map.getWidth(), h = map.getHeight();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h - 1; j++) {
                if (visited[i][j])
                    continue;
                Block b = map.getBlock(i, j, false);
                if (b.canBeCollidedWith())
                    continue;

                List<Point> points = new ArrayList<>();

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
                        pos.x += WIDTH - bw;
                        free = testCollision(map, dynamic);
                    }

                    if (free) {
                        visited[x][y - 1] = true;
                        points.add(new Point(x, y - 1));

                        if (x == w - 1)
                            break;
                        else
                            under = map.getBlock(x + 1, y, false);
                    } else break;
                }

                if (points.size() > 0) {

                    System.out.println(points);

                    platforms.add(new Platform(points));
                }
            }
        }

        return platforms;
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

    private static class Platform {

        private final List<Point> points;

        private Platform(List<Point> points) {
            this.points = points;
        }


    }

    private static class GridGraph {

        private final int[][] grid;
        private final List<GraphNode> nodes;

        public GridGraph(boolean[][] bitmap) {
            int w = bitmap.length;
            int h = bitmap[0].length;
            grid = new int[w][h];
            nodes = new ArrayList<>();

            int index = 0;
            for (int i = 0; i < w; i++)
                for (int j = 0; j < h; j++)
                    if (bitmap[i][j]) {
                        grid[i][j] = index++;
                        nodes.add(new GraphNode(i, j));
                    } else grid[i][j] = -1;
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
        private int platformID;

        public GraphNode(int x, int y) {
            this.x = x;
            this.y = y;
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