#!groovy

@Library('pipelib')
import org.veupathdb.lib.Builder

node('centos8') {
  sh "env"

  def builder = new Builder(this)

  builder.gitClone()
  builder.buildContainers([
    [ name: 'user-dataset-import-service' ],
    [ name: 'user-dataset-import-datastore', dockerfile: "pgDockerfile"]
  ])
}
