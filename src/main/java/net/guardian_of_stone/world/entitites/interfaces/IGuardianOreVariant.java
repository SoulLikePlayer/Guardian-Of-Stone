package net.guardian_of_stone.world.entitites.interfaces;

import net.guardian_of_stone.world.entitites.OreVariant;

/**
 * Manages the Guardian's cosmetic ore variant — the overlay texture rendered on
 * top of the base stone skin.
 *
 * <h2>Variants</h2>
 * <p>Each Guardian spawns with a randomly chosen {@link OreVariant} (see
 * {@link OreVariant#random(net.minecraft.util.RandomSource)}). The variant is
 * purely cosmetic: it changes the overlay texture but has no effect on stats or
 * behaviour. {@link OreVariant#NONE} means no overlay is rendered.</p>
 *
 * <h2>Persistence</h2>
 * <p>The variant is stored in NBT under the key {@code "OreVariant"} as a single
 * byte (see {@link OreVariant#toId()} / {@link OreVariant#fromId(byte)}) so it
 * survives world saves and chunk unloading.</p>
 *
 * <h2>Client synchronization</h2>
 * <p>The variant byte is synchronized via {@code DATA_ORE_VARIANT} so the client
 * renderer can pick the correct overlay texture without an extra round-trip.</p>
 */
public interface IGuardianOreVariant {

    /**
     * Returns the Guardian's current ore variant.
     *
     * @return the active {@link OreVariant}; never {@code null}
     */
    OreVariant getOreVariant();

    /**
     * Sets the Guardian's ore variant and synchronizes the change to all
     * tracking clients.
     *
     * @param variant the variant to apply; use {@link OreVariant#NONE} to remove
     *                the overlay
     */
    void setOreVariant(OreVariant variant);
}