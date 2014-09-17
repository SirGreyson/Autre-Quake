/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package eu.autremonde.quake.match;

public enum Stage {

    WAITING, STARTING, FORCE_STARTING, RUNNING, ENDING, DISABLING, ERROR;

    public boolean isRunnable() {
        return this == STARTING || this == FORCE_STARTING || this == RUNNING || this == ENDING;
    }

    public boolean isJoinable() {
        return this == WAITING || this == STARTING || this == FORCE_STARTING;
    }
}
