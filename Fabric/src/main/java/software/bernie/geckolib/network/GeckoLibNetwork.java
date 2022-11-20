package software.bernie.geckolib.network;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.network.packet.*;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Network handling class for GeckoLib.<br>
 * Handles packet registration and some networking functions
 */
public final class GeckoLibNetwork {
	public static final ResourceLocation ANIM_DATA_SYNC_PACKET_ID = new ResourceLocation(GeckoLib.ModID, "anim_data_sync");
	public static final ResourceLocation ANIM_TRIGGER_SYNC_PACKET_ID = new ResourceLocation(GeckoLib.ModID, "anim_trigger_sync");

	public static final ResourceLocation ENTITY_ANIM_DATA_SYNC_PACKET_ID = new ResourceLocation(GeckoLib.ModID, "entity_anim_data_sync");
	public static final ResourceLocation ENTITY_ANIM_TRIGGER_SYNC_PACKET_ID = new ResourceLocation(GeckoLib.ModID, "entity_anim_trigger_sync");

	public static final ResourceLocation BLOCK_ENTITY_ANIM_DATA_SYNC_PACKET_ID = new ResourceLocation(GeckoLib.ModID, "block_entity_anim_data_sync");
	public static final ResourceLocation BLOCK_ENTITY_ANIM_TRIGGER_SYNC_PACKET_ID = new ResourceLocation(GeckoLib.ModID, "block_entity_anim_trigger_sync");

	public static final Map<String, GeoAnimatable> SYNCED_ANIMATABLES = new Object2ObjectOpenHashMap<>();

	/** Used to register packets that the server sends **/
	private static void registerClientPackets(){
		ClientPlayNetworking.registerGlobalReceiver(ANIM_DATA_SYNC_PACKET_ID, AnimDataSyncPacket::receive);
		ClientPlayNetworking.registerGlobalReceiver(ANIM_TRIGGER_SYNC_PACKET_ID, AnimTriggerPacket::receive);

		ClientPlayNetworking.registerGlobalReceiver(ENTITY_ANIM_DATA_SYNC_PACKET_ID, EntityAnimDataSyncPacket::receive);
		ClientPlayNetworking.registerGlobalReceiver(ENTITY_ANIM_TRIGGER_SYNC_PACKET_ID, EntityAnimTriggerPacket::receive);

		ClientPlayNetworking.registerGlobalReceiver(BLOCK_ENTITY_ANIM_DATA_SYNC_PACKET_ID, BlockEntityAnimDataSyncPacket::receive);
		ClientPlayNetworking.registerGlobalReceiver(BLOCK_ENTITY_ANIM_TRIGGER_SYNC_PACKET_ID, BlockEntityAnimTriggerPacket::receive);
	}

	/**
	 * Registers a synced {@link GeoAnimatable} object for networking support.<br>
	 * It is recommended that you don't call this directly, instead implementing and calling {@link software.bernie.geckolib.animatable.SingletonGeoAnimatable#registerSyncedAnimatable}
	 */
	synchronized public static void registerSyncedAnimatable(GeoAnimatable animatable) {
		GeoAnimatable existing = SYNCED_ANIMATABLES.put(animatable.getClass().toString(), animatable);

		if (existing == null)
			GeckoLib.LOGGER.debug("Registered SyncedAnimatable for " + animatable.getClass().toString());
	}

	/**
	 * Gets a registered synced {@link GeoAnimatable} object by name
	 * @param className
	 */
	@Nullable
	public static GeoAnimatable getSyncedAnimatable(String className) {
		GeoAnimatable animatable = SYNCED_ANIMATABLES.get(className);

		if (animatable == null)
			GeckoLib.LOGGER.error("Attempting to retrieve unregistered synced animatable! (" + className + ")");

		return animatable;
	}

	public static void sendToTrackingEntityAndSelf(AbstractPacket packet, Entity entityToTrack){
		for(ServerPlayer trackingPlayer : PlayerLookup.tracking(entityToTrack)){
			ServerPlayNetworking.send(trackingPlayer, packet.getPacketID(), packet.encode());
		}
	}

	public static void sendToTrackingChunkAndSelf(AbstractPacket packet, ServerLevel level, BlockPos blockPos){
		for(ServerPlayer trackingPlayer : PlayerLookup.tracking(level, blockPos)){
			ServerPlayNetworking.send(trackingPlayer, packet.getPacketID(), packet.encode());
		}
	}
}
