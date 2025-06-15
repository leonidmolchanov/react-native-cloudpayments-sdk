export const isValidExpDate = (expDate: string): boolean => {
  if (typeof expDate !== 'string' || expDate.length !== 5) return false;

  const [monthStr, yearStr] = expDate.split('/');
  if (!monthStr || !yearStr) return false;

  const month = parseInt(monthStr, 10);
  const year = parseInt(yearStr, 10);

  if (isNaN(month) || isNaN(year) || month < 1 || month > 12) return false;

  const currentDate = new Date();
  const currentYear = currentDate.getFullYear() % 100;
  const currentMonth = currentDate.getMonth() + 1;

  return year > currentYear || (year === currentYear && month >= currentMonth);
};
