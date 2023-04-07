package com.easyvisa

import com.easyvisa.utils.ExceptionUtils
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional
import net.coobird.thumbnailator.Thumbnails
import org.apache.commons.io.FilenameUtils
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.multipart.MultipartFile

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes

@Transactional
@GrailsCompileStatic
class FileService {

    public static final String USERS_FOLDER_PREFIX = 'users'
    public static final String ORGANIZATIONS_FOLDER_PREFIX = 'organizations'
    public static final String PACKAGES_FOLDER_PREFIX = 'packages'
    public static final String QUARANTINE_FOLDER_PREFIX = 'quarantine'
    public static final List<String> IMAGE_SUPPORTED_FILES = ['jpg', 'jpeg', 'png', 'tiff', 'bmp']
    public static final List<String> ALL_SUPPORTED_FILES = ['jpg', 'jpeg', 'png', 'tiff', 'tif', 'bmp', 'doc', 'docx',
                                                            'pdf', 'xls', 'xlsx', 'pict', 'pct']
    public static final String POSTFIX_THUMB = '_thumb'

    @Value('${easyvisa.maxFileSizeLimit}')
    Long maxFileSizeLimit
    @Value('${easyvisa.uploadDirectory}')
    String uploadDirectory


    EasyVisaFile validateAndUploadFile(MultipartFile multipartFile, Profile uploader, String subDirectoryPath, EasyVisaFile easyVisaFile = null) {
        validateAllSupportedFiles(multipartFile)
        uploadFile(multipartFile, uploader, subDirectoryPath, easyVisaFile)
    }

    EasyVisaFile validateAndUploadImage(MultipartFile multipartFile, Profile uploader, String subDirectoryPath, EasyVisaFile easyVisaFile = null) {
        commonFileValidation(multipartFile, IMAGE_SUPPORTED_FILES)

        // Important: Should remove the existing profile file, only if all the validations are success
        if (easyVisaFile) {
            getFile(easyVisaFile)?.delete()
        }
        uploadFile(multipartFile, uploader, subDirectoryPath, easyVisaFile)
    }

    EasyVisaFile uploadRetainer(MultipartFile multipartFile, Profile uploader, Package aPackage) {
        validateAllSupportedFiles(multipartFile)
        EasyVisaFile oldRetainer = aPackage.retainerAgreement
        EasyVisaFile file = uploadFile(multipartFile, uploader,
                "${USERS_FOLDER_PREFIX}/${uploader.id}/retainer/${aPackage.id}")
        aPackage.retainerAgreement = file
        aPackage.save(failOnError: true)
        if (oldRetainer) {
            File oldFile = new File("${uploadDirectory}/${oldRetainer?.path}")
            oldRetainer.delete(failOnError: true)
            oldFile.delete()
        }
        file
    }

    File getFile(EasyVisaFile easyVisaFile) {
        if (easyVisaFile.path) {
            String easyVisaFilePath = "${uploadDirectory}/${easyVisaFile?.path}"
            Path path = Paths.get(easyVisaFilePath)
            if (Files.notExists(path)) {
                throw ExceptionUtils.createNotFoundException('ev.file.not.found');
            }
            new File(easyVisaFilePath)
        }
    }

    File getThumbnailFile(EasyVisaFile easyVisaFile) {
        if (easyVisaFile.thumbnailPath) {
            new File("${uploadDirectory}/${easyVisaFile?.thumbnailPath}")
        }
    }

    void deleteEasyVisaFile(EasyVisaFile easyVisaFile) {
        if (easyVisaFile?.path) {
            new File("${uploadDirectory}/${easyVisaFile.path}").delete()
        }
        if (easyVisaFile?.thumbnailPath) {
            new File("${uploadDirectory}/${easyVisaFile.thumbnailPath}").delete()
        }
        easyVisaFile?.delete(failOnError: true)
    }

    Package deleteRetainer(Package aPackage, Boolean skipValidation = Boolean.FALSE) {
        if (aPackage?.retainerAgreement) {
            EasyVisaFile easyVisaFile = aPackage.retainerAgreement
            File file = new File("${uploadDirectory}/${easyVisaFile?.path}")
            aPackage.retainerAgreement = null
            aPackage.save(failOnError: true)
            easyVisaFile.delete(failOnError: true)
            file.delete() // Delete the file from filesystem as well
            aPackage
        } else {
            if (!skipValidation) {
                throw ExceptionUtils.createUnProcessableDataException('package.retainer.not.found')
            }
        }
    }

    File getPackageRetainer(Package aPackage) {
        if (aPackage.retainerAgreement) {
            new File("${uploadDirectory}/${aPackage.retainerAgreement.path}")
        }
    }

    EasyVisaFile addProfilePhoto(Profile profile, MultipartFile file) {
        validateAndUploadImage(file, profile,
                "${USERS_FOLDER_PREFIX}/${profile.id}/profilephoto", profile.profilePhoto)
    }

    EasyVisaFile addOrganizationPhoto(Organization organization, MultipartFile file, Profile uploadedBy) {
        validateAndUploadImage(file, uploadedBy, "${ORGANIZATIONS_FOLDER_PREFIX}/${organization.id}/profilephoto",
                organization.logoFile)
    }

    /**
     * Checks if there are outdated files in quarantine folder.
     * If so, they will be deleted.
     */
    void deleteOutdatedQuarantineFiles() {
        new File("${uploadDirectory}/${QUARANTINE_FOLDER_PREFIX}").eachFile {
            if (!it.isDirectory()
                    && (Files.readAttributes(it.toPath(), BasicFileAttributes.class).creationTime().toMillis()
                    < DateTime.now().minusDays(1).millis)) {
                log.info("Deleting file [${it.name}]")
                it.delete()
            }
        }
    }

    String getQurantineFolderPath() {
        return "${uploadDirectory}/${QUARANTINE_FOLDER_PREFIX}"
    }

    String combinePackageFileFolder(Long packageId, Long applicantId) {
        return "${PACKAGES_FOLDER_PREFIX}/${packageId}/${applicantId}"
    }

    String combinePackageFilePath(String subFolder, String fileName, String extension, String filePostfix = '') {
        return "${subFolder}/${fileName}${(filePostfix) ? filePostfix : ''}.${extension}"
    }

    private EasyVisaFile uploadFile(MultipartFile multipartFile, Profile uploadedBy, String subDirectoryPath,
                                    EasyVisaFile easyVisaFile = null) {
        String fileName = UUID.randomUUID().toString()
        String originalFilename = multipartFile.originalFilename
        String extension = FilenameUtils.getExtension(originalFilename)?.toLowerCase()
        String filePath = combinePackageFilePath(subDirectoryPath, fileName, extension)
//        String filePath = "${subDirectoryPath}/${fileName}.${extension}"
        Files.createDirectories(Paths.get("${uploadDirectory}/${subDirectoryPath}"))
        File file = new File("${uploadDirectory}/${filePath}")
        multipartFile.transferTo(file)
        easyVisaFile = easyVisaFile ?: new EasyVisaFile()
        easyVisaFile.with {
            path = filePath
            originalName = originalFilename
            uploader = uploadedBy
            fileType = extension
        }
        if(validateFileType(multipartFile, IMAGE_SUPPORTED_FILES)){
            String thumbnailFilePath = combinePackageFilePath(subDirectoryPath, fileName, extension, POSTFIX_THUMB)
//            String thumbnailFilePath = "${subDirectoryPath}/${fileName}_thumb.${extension}"
            Thumbnails.of(file)
                    .size(90, 90)
                    .toFile(new File("${uploadDirectory}/${thumbnailFilePath}"));
            easyVisaFile.thumbnailPath = thumbnailFilePath;
        }
        easyVisaFile.save(failOnError: true)
    }

    EasyVisaFile copyFile(EasyVisaFile toCopy, Package newPackage, Applicant newApplicant) {
        String fileName = UUID.randomUUID().toString()
        String extension = FilenameUtils.getExtension(toCopy.path)
        String folder = combinePackageFileFolder(newPackage.id, newApplicant.id)
        String path = combinePackageFilePath(folder, fileName, extension)
        Files.createDirectories(Paths.get("${uploadDirectory}/${folder}"))
        Files.copy(Paths.get(uploadDirectory, toCopy.path), Paths.get(uploadDirectory, path))
        String thumbPath = null
        if (toCopy.thumbnailPath) {
            thumbPath = combinePackageFilePath(folder, fileName, extension, POSTFIX_THUMB)
            Files.copy(Paths.get(uploadDirectory, toCopy.thumbnailPath), Paths.get(uploadDirectory, thumbPath))
        }
        EasyVisaFile copy = toCopy.copy(path, thumbPath)
        copy
    }

    private void validateAllSupportedFiles(MultipartFile multipartFile) {
        commonFileValidation(multipartFile, ALL_SUPPORTED_FILES)
    }

    private void commonFileValidation(MultipartFile multipartFile, List<String> allowedFileTypes) {
        if (!validateFileSize(multipartFile)) {
            throw ExceptionUtils.createUnProcessableDataException('file.size.exceeds.limit')
        }

        if (!validateFileType(multipartFile, allowedFileTypes)) {
            throw ExceptionUtils.createUnProcessableDataException('file.type.not.allowed', null, [allowedFileTypes.join(", ").toUpperCase()])
        }
    }

    private Boolean validateFileType(MultipartFile multipartFile, List<String> allowedFileTypes) {
        String originalFilename = multipartFile.originalFilename
        String fileExt = FilenameUtils.getExtension(originalFilename)
        allowedFileTypes.any { it.equalsIgnoreCase(fileExt) }
    }

    private Boolean validateFileSize(MultipartFile file) {
        file.size < maxFileSizeLimit
    }

}
