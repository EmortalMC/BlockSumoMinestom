package dev.emortal.bs.item

import dev.emortal.bs.game.BlockSumoGame
import dev.emortal.bs.util.RaycastResultType
import dev.emortal.bs.util.RaycastUtil
import net.kyori.adventure.sound.Sound
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import net.minestom.server.sound.SoundEvent
import world.cepi.kstom.adventure.asMini
import world.cepi.kstom.util.asVec
import world.cepi.kstom.util.eyePosition
import world.cepi.kstom.util.playSound
import world.cepi.particle.Particle
import world.cepi.particle.ParticleType
import world.cepi.particle.data.OffsetAndSpeed
import world.cepi.particle.renderer.Renderer
import world.cepi.particle.showParticle

object Switcheroo : Powerup(
    "<rainbow>Switcheroo".asMini(),
    "switchyboi", // thanks SLL for name :)
    Material.ENDER_EYE,
    Rarity.LEGENDARY,
    PowerupInteractType.USE,
    SpawnType.MIDDLE
) {

    override fun use(game: BlockSumoGame, player: Player, hand: Player.Hand, pos: Pos?, entity: Entity?) {
        removeOne(player, hand)

        player.instance!!.playSound(
            Sound.sound(SoundEvent.BLOCK_BEACON_ACTIVATE, Sound.Source.PLAYER, 1f, 1f),
            player.position
        )

        val raycast = RaycastUtil.raycast(
            game,
            player.eyePosition(),
            player.position.direction(),
            60.0,
        ) { it is Player && it.gameMode == GameMode.SURVIVAL && it != player }

        game.showParticle(
            Particle.particle(
                type = ParticleType.END_ROD,
                count = 1,
                data = OffsetAndSpeed(),
            ),
            Renderer.fixedLine(
                player.eyePosition().asVec(),
                (raycast.hitPosition ?: player.eyePosition().add(player.position.direction().mul(20.0))).asVec()
            )
        )

        if (raycast.resultType == RaycastResultType.HIT_ENTITY) {
            val entity = raycast.hitEntity!!
            val entityPos = entity.position
            val entityVelocity = entity.velocity

            val playerPos = player.position
            val playerVelocity = player.velocity

            entity.teleport(playerPos)
            entity.velocity = playerVelocity

            player.teleport(entityPos)
            player.velocity = entityVelocity

            player.instance!!.playSound(
                Sound.sound(SoundEvent.ENTITY_ENDERMAN_TELEPORT, Sound.Source.PLAYER, 1f, 1f),
                player.position
            )
            entity.instance!!.playSound(
                Sound.sound(SoundEvent.ENTITY_ENDERMAN_TELEPORT, Sound.Source.PLAYER, 1f, 1f),
                entity.position
            )
        }
    }

}