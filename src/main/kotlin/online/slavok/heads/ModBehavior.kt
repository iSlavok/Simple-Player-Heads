package online.slavok.heads

/**
 * Test seam for gametests. Production always leaves this `true`; a gametest flips it to `false`
 * to assert the vanilla (no head) outcome as an A/B negative control, proving the head drop is
 * caused by this mod's code rather than something incidental.
 */
object ModBehavior {
    @JvmField
    var active: Boolean = true
}
