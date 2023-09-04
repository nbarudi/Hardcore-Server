package ca.bungo.hardcore.utility;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class MathUtility {

    private static double distanceSquared(Vector from, Vector to)
    {
        double dx = to.getBlockX() - from.getBlockX();
        double dz = to.getBlockZ() - from.getBlockZ();

        return dx * dx + dz * dz;
    }

    public static Vector calculateArc(Location starting, Location goal, int heightGain){

        Vector from = starting.toVector();
        Vector to = goal.toVector();

        // Gravity of a potion
        double gravity = 0.115;

        // Block locations
        int endGain = to.getBlockY() - from.getBlockY();
        double horizDist = Math.sqrt(distanceSquared(from, to));

        // Height gain
        double maxGain = Math.max(heightGain, (endGain + heightGain));

        // Solve quadratic equation for velocity
        double a = -horizDist * horizDist / (4 * maxGain);
        double c = -endGain;

        double slope = -horizDist / (2 * a) - Math.sqrt(horizDist * horizDist - 4 * a * c) / (2 * a);

        // Vertical velocity
        double vy = Math.sqrt(maxGain * gravity);

        // Horizontal velocity
        double vh = vy / slope;

        // Calculate horizontal direction
        int dx = to.getBlockX() - from.getBlockX();
        int dz = to.getBlockZ() - from.getBlockZ();
        double mag = Math.sqrt(dx * dx + dz * dz);
        double dirx = dx / mag;
        double dirz = dz / mag;

        // Horizontal velocity components
        double vx = vh * dirx;
        double vz = vh * dirz;

        return new Vector(vx, vy, vz);
    }

}
