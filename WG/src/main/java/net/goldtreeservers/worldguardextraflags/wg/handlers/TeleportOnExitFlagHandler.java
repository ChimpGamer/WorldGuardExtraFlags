package net.goldtreeservers.worldguardextraflags.wg.handlers;

import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;

import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;
import org.bukkit.plugin.Plugin;

public class TeleportOnExitFlagHandler extends FlagValueChangeHandler<Location>
{
	public static Factory FACTORY(Plugin plugin)
	{
		return new Factory(plugin);
	}
	
    public static class Factory extends Handler.Factory<TeleportOnExitFlagHandler>
    {
		private final Plugin plugin;

		public Factory(Plugin plugin)
		{
			this.plugin = plugin;
		}

		@Override
        public TeleportOnExitFlagHandler create(Session session)
        {
            return new TeleportOnExitFlagHandler(this.plugin, session);
        }
    }

	private final Plugin plugin;
	   
	protected TeleportOnExitFlagHandler(Plugin plugin, Session session)
	{
		super(session, Flags.TELEPORT_ON_EXIT);

		this.plugin = plugin;
	}

	@Override
	protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, Location value)
	{
	}

	@Override
	protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Location currentValue, Location lastValue, MoveType moveType)
	{
		this.handleValue(player, (World) from.getExtent(), lastValue);
		return true;
	}

	@Override
	protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Location lastValue, MoveType moveType)
	{
		this.handleValue(player, (World) from.getExtent(), lastValue);
		return true;
	}

	public void handleValue(LocalPlayer player, World world, Location value)
	{
		if (this.getSession().getManager().hasBypass(player, world))
		{
			return;
		}

		if (value != null && WorldGuardUtils.hasNoTeleportLoop(this.plugin, ((BukkitPlayer) player).getPlayer(), value))
		{
			player.setLocation(value);
		}
	}
}
