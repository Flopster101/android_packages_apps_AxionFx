/*
 * Copyright 2025-2026 AxionOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "TransientShaper.h"
#include <algorithm>
#include <cmath>

namespace axionfx {

void TransientShaper::configure(float sampleRate) {
    mSampleRate = sampleRate;
    updateCoefficients();
    reset();
}

void TransientShaper::updateCoefficients() {
    if (mSampleRate <= 0.0f) return;

    // Fast follower (1.0 ms attack, 25.0 ms release)
    float attackFastSamples = (1.0f / 1000.0f) * mSampleRate;
    float releaseFastSamples = (25.0f / 1000.0f) * mSampleRate;
    mCoeffAttackFast = std::exp(-1.0f / attackFastSamples);
    mCoeffReleaseFast = std::exp(-1.0f / releaseFastSamples);

    // Slow follower (15.0 ms attack, 150.0 ms release)
    float attackSlowSamples = (15.0f / 1000.0f) * mSampleRate;
    float releaseSlowSamples = (150.0f / 1000.0f) * mSampleRate;
    mCoeffAttackSlow = std::exp(-1.0f / attackSlowSamples);
    mCoeffReleaseSlow = std::exp(-1.0f / releaseSlowSamples);
}

void TransientShaper::process(float* buffer, int frames) {
    if (!mEnabled || frames <= 0) return;

    for (int f = 0; f < frames; ++f) {
        float l = buffer[f * 2];
        float r = buffer[f * 2 + 1];
        float peak = std::max(std::fabs(l), std::fabs(r));

        // Fast envelope follower
        float cf = (peak > mEnvFast) ? mCoeffAttackFast : mCoeffReleaseFast;
        mEnvFast = cf * mEnvFast + (1.0f - cf) * peak;
        mEnvFast = std::max(mEnvFast, 1e-6f);

        // Slow envelope follower
        float cs = (peak > mEnvSlow) ? mCoeffAttackSlow : mCoeffReleaseSlow;
        mEnvSlow = cs * mEnvSlow + (1.0f - cs) * peak;
        mEnvSlow = std::max(mEnvSlow, 1e-6f);

        // Transient ratio (with safety epsilon of 0.01f to avoid silence boost / div-by-zero)
        float transient = (mEnvFast - mEnvSlow) / (mEnvFast + 0.01f);
        transient = std::max(0.0f, transient);

        // Attack gain: -100% to +100% maps to -1.0f to +1.0f
        float attackGain = 1.0f + mAttackAmt * transient * 2.0f;
        attackGain = std::max(0.0f, attackGain);

        // Sustain gain: -100% to +100% maps to -1.0f to +1.0f
        float sustain = mEnvSlow / (mEnvFast + 0.01f);
        sustain = std::min(1.0f, sustain);
        float sustainGain = 1.0f + mSustainAmt * sustain * 2.0f;
        sustainGain = std::max(0.0f, sustainGain);

        float totalGain = attackGain * sustainGain;
        totalGain = std::min(10.0f, totalGain); // safe clamping

        // Smooth applied gain to remove digital click/crunch/popping
        mGainSmoothed = 0.98f * mGainSmoothed + 0.02f * totalGain;

        buffer[f * 2] = l * mGainSmoothed;
        buffer[f * 2 + 1] = r * mGainSmoothed;
    }
}

void TransientShaper::setEnabled(bool enabled) {
    mEnabled = enabled;
    if (!enabled) {
        reset();
    }
}

void TransientShaper::setAttack(float percent) {
    mAttackAmt = std::clamp(percent, -1.0f, 1.0f);
}

void TransientShaper::setSustain(float percent) {
    mSustainAmt = std::clamp(percent, -1.0f, 1.0f);
}

void TransientShaper::reset() {
    mEnvFast = 0.0f;
    mEnvSlow = 0.0f;
    mGainSmoothed = 1.0f;
}

}  // namespace axionfx
