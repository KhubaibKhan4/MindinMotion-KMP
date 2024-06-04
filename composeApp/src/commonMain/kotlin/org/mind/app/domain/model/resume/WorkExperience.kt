package org.mind.app.domain.model.resume

data class WorkExperience(
    var companyName: String = "",
    var jobTitle: String = "",
    var startDate: String = "",
    var endDate: String = ""
)

data class Education(
    var degreeName: String = "",
    var universityName: String = "",
    var sessionYear: String = ""
)