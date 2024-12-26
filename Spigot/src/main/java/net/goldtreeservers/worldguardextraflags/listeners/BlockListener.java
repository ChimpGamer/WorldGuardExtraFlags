package net.goldtreeservers.worldguardextraflags.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.session.SessionManager;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.EntityBlockFormEvent;

import com.sk89q.worldguard.protection.flags.StateFlag.State;

import lombok.RequiredArgsConstructor;
import net.goldtreeservers.worldguardextraflags.flags.Flags;

import java.util.Set;

@RequiredArgsConstructor
public class BlockListener implements Listener
{
	private final WorldGuardPlugin worldGuardPlugin;
	private final RegionContainer regionContainer;
	private final SessionManager sessionManager;
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityBlockFormEvent(EntityBlockFormEvent event)
	{
		BlockState newState = event.getNewState();
		if (newState.getType() == Material.FROSTED_ICE)
		{
			Location location = BukkitAdapter.adapt(newState.getLocation());

			LocalPlayer localPlayer;
			if (event.getEntity() instanceof Player player)
			{
				localPlayer = this.worldGuardPlugin.wrapPlayer(player);
				if (this.sessionManager.hasBypass(localPlayer, (World) location.getExtent()))
				{
					return;
				}
			}
			else
			{
				localPlayer = null;
			}

			if (this.regionContainer.createQuery().queryValue(location, localPlayer, Flags.FROSTWALKER) == State.DENY)
			{
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockDropItem(BlockDropItemEvent event) {
		LocalPlayer localPlayer = worldGuardPlugin.wrapPlayer(event.getPlayer());
		Location location = BukkitAdapter.adapt(event.getBlock().getLocation());
		if (this.sessionManager.hasBypass(localPlayer, (World) location.getExtent())) {
			return;
		}

		Set<Material> allowedDrops = regionContainer.createQuery().queryValue(location, localPlayer, Flags.ALLOWED_BLOCK_DROPS);
		if (!event.getItems().removeIf(item -> allowedDrops != null && !allowedDrops.contains(item.getItemStack().getType()))) {
			Set<Material> blockedDrops = regionContainer.createQuery().queryValue(location, localPlayer, Flags.BLOCKED_BLOCK_DROPS);
			event.getItems().removeIf(item -> blockedDrops != null && blockedDrops.contains(item.getItemStack().getType()));
		}
	}
}
