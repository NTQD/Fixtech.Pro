export function parseDuration(durationStr: string): number {
    if (!durationStr) return 60; // Default to 60 mins if missing

    const lower = durationStr.toLowerCase().trim();
    let totalMinutes = 0;

    // Regex for "X hours" or "X h" or "X tiếng"
    const hoursMatch = lower.match(/(\d+)\s*(h|hour|hours|tiếng|giờ)/);
    if (hoursMatch) {
        totalMinutes += parseInt(hoursMatch[1]) * 60;
    }

    // Regex for "X minutes" or "X m" or "X mins" or "X phút"
    const minsMatch = lower.match(/(\d+)\s*(m|min|mins|minute|minutes|phút)/);
    if (minsMatch) {
        totalMinutes += parseInt(minsMatch[1]);
    }

    // Fallback: if user just typed a number "30", assume minutes? Or if valid hours found but 0 total, return parsed
    // If no matches found but it's a number, assume minutes
    if (totalMinutes === 0) {
        const justNumber = parseInt(lower);
        if (!isNaN(justNumber)) {
            return justNumber; // Assume minutes if just a number
        }
        return 60; // Safe default
    }

    return totalMinutes;
}
