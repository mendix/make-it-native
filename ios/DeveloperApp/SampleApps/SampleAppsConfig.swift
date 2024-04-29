import Foundation

class SampleAppsConfig {
  func read() -> [SampleApp] {
    return read(url: configFileUrl(), configName: "downloaded")
      ?? read(url: Bundle.main.url(forResource: "config", withExtension: "json"), configName: "bundled")
      ?? []
  }
  
  private func read(url: URL?, configName: String) -> [SampleApp]? {
    do {
      let config = try JSONDecoder().decode(SampleAppsRoot.self, from: Data(contentsOf: url!))
      return config.apps
    } catch {
      return nil
    }
  }
  
  func checkForUpdate() {
    guard let url = updateUrl() else {
      return
    }
    
    URLSession.shared.dataTask(with: url, completionHandler: {(data, response, error) in
      guard error == nil else {
        return
      }
      
      if let data = data, let configFilePath = self.configFileUrl() {
        do {
          try FileManager.default.createDirectory(at: configFilePath.deletingLastPathComponent(), withIntermediateDirectories: true, attributes: nil)
          
          try data.write(to: configFilePath, options: NSData.WritingOptions.atomic)
          
        } catch {

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
