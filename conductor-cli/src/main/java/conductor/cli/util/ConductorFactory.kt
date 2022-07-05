package conductor.cli.util

import conductor.Conductor
import conductor.utils.SocketUtils.isPortInUse
import dadb.Dadb

object ConductorFactory {

    fun createConductor(target: String?): Conductor {
        return when (target) {
            "android" -> {
                val dadb = Dadb.discover("localhost")
                if (dadb == null) {
                    println("No Android devices found")
                    throw IllegalStateException()
                }

                Conductor.android(dadb)
            }
            "ios" -> {
                Conductor.ios("localhost", 10882)
            }
            null -> findDevice()
            else -> throw IllegalStateException("Unknown target: $target")
        }
    }

    private fun findDevice(): Conductor {
        var device: Conductor? = null

        val dadb = Dadb.discover("localhost")
        if (dadb != null) {
            device = Conductor.android(dadb)
        }

        if (isPortInUse("localhost", 10882)) {
            if (device == null) {
                return Conductor.ios("localhost", 10882)
            } else {
                throw IllegalStateException("Multiple devices found. Specify one with --target")
            }
        }

        if (isPortInUse("localhost", 10883)) {
            if (device == null) {
                return Conductor.ios("localhost", 10883)
            } else {
                throw IllegalStateException("Multiple devices found. Specify one with --target")
            }
        }

        return device ?: throw IllegalStateException("No device found")
    }

}