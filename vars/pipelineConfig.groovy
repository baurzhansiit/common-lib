def call() {
  def pipelineConfig = readYaml(file: "${WORKSPACE}/myconfig.yml")
  return pipelineConfig
}
