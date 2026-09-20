export function getColorForPercent(percent) {
  if (percent < 50) return 'var(--color-success)';
  if (percent < 80) return 'var(--color-warning)';
  return 'var(--color-critical)';
}