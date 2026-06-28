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

#pragma once

namespace axionfx {

class TransientShaper {
public:
    void configure(float sampleRate);
    void process(float* buffer, int frames);
    void setEnabled(bool enabled);
    bool isEnabled() const { return mEnabled; }

    void setAttack(float percent);  // Range -1.0f to 1.0f
    void setSustain(float percent); // Range -1.0f to 1.0f

    void reset();

private:
    void updateCoefficients();

    bool mEnabled = false;
    float mSampleRate = 48000.0f;

    float mAttackAmt = 0.0f;  // -1.0f to 1.0f
    float mSustainAmt = 0.0f; // -1.0f to 1.0f

    // Envelope followers
    float mEnvFast = 0.0f;
    float mEnvSlow = 0.0f;
    float mGainSmoothed = 1.0f;

    // Coefficients
    float mCoeffAttackFast = 0.0f;
    float mCoeffReleaseFast = 0.0f;
    float mCoeffAttackSlow = 0.0f;
    float mCoeffReleaseSlow = 0.0f;
};

}  // namespace axionfx
