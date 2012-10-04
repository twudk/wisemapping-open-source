/*
*    Copyright [2011] [wisemapping]
*
*   Licensed under WiseMapping Public License, Version 1.0 (the "License").
*   It is basically the Apache License, Version 2.0 (the "License") plus the
*   "powered by wisemapping" text requirement on every single page;
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the license at
*
*       http://www.wisemapping.org/license
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/

package com.wisemapping.service;

import com.wisemapping.model.Collaborator;
import com.wisemapping.model.Mindmap;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class LockInfo {
    final private Collaborator collaborator;
    private Calendar timeout;
    private long session;
    private static int EXPIRATION_MIN = 30;
    private long timestamp = -1;
    private long previousTimestamp;

    public LockInfo(@NotNull Collaborator collaborator, @NotNull Mindmap mindmap, long session) {
        this.collaborator = collaborator;
        this.updateTimeout();
        this.updateTimestamp(mindmap);
    }

    public Collaborator getCollaborator() {
        return collaborator;
    }

    public boolean isExpired() {
        return timeout.before(Calendar.getInstance());
    }

    public void updateTimeout() {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, EXPIRATION_MIN);
        this.timeout = calendar;

    }

    public long getSession() {
        return session;
    }

    public void setSession(long session) {
        this.session = session;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getPreviousTimestamp() {
        return previousTimestamp;
    }

    public void updateTimestamp(@NotNull Mindmap mindmap) {
        this.previousTimestamp = this.timestamp;
        this.timestamp = mindmap.getLastModificationTime().getTimeInMillis();
    }
}
