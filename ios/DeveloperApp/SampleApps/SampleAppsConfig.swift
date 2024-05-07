import Foundation

class SampleAppsConfig {
//  private let logger = Logger.create("SampleApps")
  
  func read() -> [SampleApp] {
    return read(url: configFileUrl(), configName: "downloaded")
      ?? read(url: Bundle.main.url(forResource: "config", withExtension: "json"), configName: "bundled")
      ?? []
  }
  
  private func read(url: URL?, configName: String) -> [SampleApp]? {
    do {
      let config = try JSONDecoder().decode(SampleAppsRoot.self, from: Data(contentsOf: url!))
//      logger.default("Found %{PUBLIC}@ config file, it contains %{PUBLIC}d app(s)", configName, config.apps.count)
      return config.apps
    } catch {
//      logger.error("Unable to read the %{PUBLIC}@ config file: %{PUBLIC}@", configName, error.localizedDescription)
      return nil
    }
  }
  
  func checkForUpdate() {
//    logger.default("Checking for updates...")
    
    guard let url = updateUrl() else {
//      logger.error("Unable to read the update URL from info.plist")
      return
    }
    
    URLSession.shared.dataTask(with: url, completionHandler: {(data, response, error) in
      guard error == nil else {
//        self.logger.error("Error checking for updates for the configuration file: %{PUBLIC}@", error?.localizedDescription ?? "")
        return
      }
      
      if let data = data, let configFilePath = self.configFileUrl() {
        do {
          try FileManager.default.createDirectory(at: configFilePath.deletingLastPathComponent(), withIntermediateDirectories: true, attributes: nil)
          
          try data.write(to: configFilePath, options: NSData.WritingOptions.atomic)
          
//          self.logger.default("Downloaded new configuration file")
        } catch {
//          self.logger.error("Error writing the downloaded configuration file to %{PRIVATE}@: %{PUBLIC}@", configFilePath.absoluteString, error.localizedDescription)
        }
      }
    }).resume()
  }
  
  private func updateUrl() -> URL? {
    guard let url = Bundle.main.object(forInfoDictionaryKey: "SampleAppsUpdateURL") as? String else {
      return nil
    }
    
    return URL(string: url)
  }
  
  private func configFileUrl() -> URL? {
    let applicationSupportDirectory = FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask).first
    return URL(string: "Mendix/sampleApps.json", relativeTo: applicationSupportDirectory)
  }
}
