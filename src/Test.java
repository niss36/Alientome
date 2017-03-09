import com.game.blocks.BlockBuilder;

import java.awt.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

@SuppressWarnings("ALL")
public class Test {

    public static void main(String[] args) throws Exception {

        //Level serialization experiment

        /*BlockBuilder air = new BlockBuilder(Block.AIR, (byte) 0);
        BlockBuilder sand = new BlockBuilder(Block.SAND, (byte) 0);
        BlockBuilder sandBg = new BlockBuilder(Block.SAND, (byte) 1);

        BlockBuilder[][] toWrite = {{air, air, air}, {air, air, air}, {sandBg, sandBg, sand}, {sand, sand, sand}};

        try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream("testFile"))) {

            stream.writeObject(toWrite);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream("testFile"))) {

            BlockBuilder[][] toRead = (BlockBuilder[][]) stream.readObject();

            for (BlockBuilder[] row : toRead) {
                for (BlockBuilder builder : row) {
                    System.out.print(builder + "\t");
                }
                System.out.println();
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }*/

        //Level byte-wise storage experiment

        /*BlockBuilder b1 = new BlockBuilder(BlockConstants.AIR, (byte) 0);
        BlockBuilder b2 = new BlockBuilder(BlockConstants.SAND, (byte) 0);
        BlockBuilder b3 = new BlockBuilder(BlockConstants.SAND, (byte) 1);
        BlockBuilder b4 = new BlockBuilder(BlockConstants.HOLE, (byte) 0);

        BlockBuilder[][] blocks = {{b1, b1, b1}, {b2, b3, b1}, {b2, b2, b2}, {b4, b4, b4}};

        System.out.println("Before :");
        System.out.println(Arrays.deepToString(blocks));

        OutputStream os = new FileOutputStream("testFile");

        write(blocks, os);

        os.close();

        InputStream is = new FileInputStream("testFile");

        blocks = read(is);

        is.close();

        System.out.println("After :");
        System.out.println(Arrays.deepToString(blocks));*/

        /*AxisAlignedBoundingBox box1 = new StaticBoundingBox(0, 0, 20, 30);
        AxisAlignedBoundingBox box2 = new StaticBoundingBox(15, 0, 35, 30);
        Vec2 vel1 = new Vec2(0, 0);
        Vec2 vel2 = new Vec2(-10, 0);
        System.out.println(box1.processCollisionWith(box2, vel1, vel2));*/

        /*KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {

            if (e.getID() == KeyEvent.KEY_RELEASED)
                System.out.println(e.getKeyCode());

            return true;
        });

        new JFrame().setVisible(true);*/

        String t = "@e[t=1,t=2]";
        System.out.println(t.substring(3, t.lastIndexOf(']')));
        String[] split = t.substring(3, t.lastIndexOf(']')).split(",");
        System.out.println(Arrays.toString(split));
    }

    private static void write(BlockBuilder[][] blocks, OutputStream stream) throws Exception {

        int length0 = blocks.length;
        int length1 = blocks[0].length;

        writeInt(length0, stream);
        writeInt(length1, stream);

        for (BlockBuilder[] blocks0 : blocks) {
            for (BlockBuilder block : blocks0) {

                byte[] bytes = {block.index, block.metadata};
                stream.write(bytes);
            }
        }
    }

    private static BlockBuilder[][] read(InputStream stream) throws Exception {

        int length0 = readInt(stream);
        int length1 = readInt(stream);

        BlockBuilder[][] blocks = new BlockBuilder[length0][length1];

        for (int i = 0; i < length0; i++)
            for (int j = 0; j < length1; j++) {

                byte index = (byte) stream.read();
                byte meta = (byte) stream.read();
                blocks[i][j] = new BlockBuilder(index, meta);
            }

        return blocks;
    }

    private static void writeInt(int toWrite, OutputStream stream) throws Exception {

        for (int i = 0; i < 4; i++) {

            stream.write(toWrite);
            toWrite = toWrite >> 8;
        }
    }

    private static int readInt(InputStream stream) throws Exception {

        int toRead = 0;

        for (int i = 0; i < 4; i++) {

            int b = stream.read();
            b = b << i * 8;
            toRead |= b;
        }

        return toRead;
    }
}