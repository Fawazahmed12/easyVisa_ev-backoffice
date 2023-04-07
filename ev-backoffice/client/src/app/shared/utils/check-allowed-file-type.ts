export function checkAllowedFileType(fileType, types) {
  const foundFileType = types.find((type) => fileType === type);
  return !!foundFileType;
}
