package com.easyvisa

import org.springframework.web.multipart.MultipartFile


class ProfilePictureCommand implements grails.validation.Validateable {

    MultipartFile profilePhoto
}