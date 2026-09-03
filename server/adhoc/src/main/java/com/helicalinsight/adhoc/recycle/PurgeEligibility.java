package com.helicalinsight.adhoc.recycle;

import java.util.Collections;
import java.util.Set;

public final class PurgeEligibility {

	private final Set<Long> eligible;
	private final Set<Long> blocked;

	public PurgeEligibility(Set<Long> eligible, Set<Long> blocked) {
		this.eligible = Collections.unmodifiableSet(eligible);
		this.blocked = Collections.unmodifiableSet(blocked);
	}

	public Set<Long> getEligible() {
		return eligible;
	}

	public Set<Long> getBlocked() {
		return blocked;
	}

	public boolean isBlocked(Long binId) {
		return blocked.contains(binId);
	}

	public boolean isEligible(Long binId) {
		return eligible.contains(binId);
	}
}