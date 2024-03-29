import jetbrains.buildServer.configs.kotlin.v2019_2.DslContext
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.SshAgent
import jetbrains.buildServer.configs.kotlin.v2019_2.project
import jetbrains.buildServer.configs.kotlin.v2019_2.sequential
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.VcsTrigger
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.version
import no.elhub.devxp.build.configuration.Assemble
import no.elhub.devxp.build.configuration.AutoRelease
import no.elhub.devxp.build.configuration.CodeReview
import no.elhub.devxp.build.configuration.ProjectType
import no.elhub.devxp.build.configuration.PublishDocs
import no.elhub.devxp.build.configuration.SonarScan
import no.elhub.devxp.build.configuration.UnitTest
import no.elhub.devxp.build.configuration.constants.GlobalTokens

version = "2022.10"

project {
    val projectName = "devxp-elhub-phabricator-extensions"
    val projectId = "no.elhub.devxp:$projectName"
    val projectType = ProjectType.GENERIC
    val artifactoryRepository = "elhub-bin-release-local"

    params {
        param("teamcity.ui.settings.readOnly", "true")
    }

    val sonarScanConfig = SonarScan.Config(
        vcsRoot = DslContext.settingsRoot,
        type = projectType,
        sonarId = projectId,
        sonarProjectSources = "src"
    )

    val sonarScan = SonarScan(sonarScanConfig)

    val release = AutoRelease(
        AutoRelease.Config(
            vcsRoot = DslContext.settingsRoot,
            type = projectType,
            repository = artifactoryRepository
        )
    ) {
        triggers {
            vcs {
                branchFilter = "+:<default>"
                quietPeriodMode = VcsTrigger.QuietPeriodMode.USE_DEFAULT
            }
        }

        dependencies {
            snapshot(sonarScan) { }
        }
    }

    listOf(sonarScan, release).forEach { buildType(it) }

    buildType(
        CodeReview(
            CodeReview.Config(
                vcsRoot = DslContext.settingsRoot,
                type = projectType,
                sonarScanConfig = sonarScanConfig,
            )
        )
    )

}
