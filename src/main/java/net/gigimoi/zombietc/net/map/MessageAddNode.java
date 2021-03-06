package net.gigimoi.zombietc.net.map;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.gigimoi.zombietc.ZombieTC;
import net.gigimoi.zombietc.block.BlockNode;
import net.gigimoi.zombietc.util.pathfinding.MCNode;
import net.minecraft.util.Vec3;

/**
 * Created by gigimoi on 7/16/2014.
 */
public class MessageAddNode implements IMessage {
    Vec3 pos;

    public MessageAddNode() {
    }

    public MessageAddNode(int x, int y, int z) {
        pos = Vec3.createVectorHelper(x, y, z);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = Vec3.createVectorHelper(buf.readInt(), buf.readInt(), buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt((int) pos.xCoord);
        buf.writeInt((int) pos.yCoord);
        buf.writeInt((int) pos.zCoord);
    }

    public static class MessageAddNodeHandler implements IMessageHandler<MessageAddNode, MessageAddNode> {
        @Override
        public MessageAddNode onMessage(MessageAddNode message, MessageContext ctx) {
            BlockNode.nodes.add(new MCNode(message.pos));
            ZombieTC.gameManager.regeneratePathMap();
            if (ctx.side == Side.SERVER) {
                ZombieTC.network.sendToAll(message);
            }
            return null;
        }
    }
}
