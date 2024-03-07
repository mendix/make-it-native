//
//  URLImage.swift
//  DeveloperApp
//
//  Created by Selim Ustel on 19/06/2023.
//  Copyright © 2023 Mendix. All rights reserved.
//

import SwiftUI
import Combine
import Foundation

struct ImageView: View {
  @ObservedObject var imageLoader:ImageLoader
  @State var image:UIImage = UIImage()
  let height:CGFloat
  let width:CGFloat
  
  init(withURL url:String, height:CGFloat, width: CGFloat) {
    self.height = height
    self.width = width
    imageLoader = ImageLoader(urlString:url)
  }
  
  var body: some View {
    Image(uiImage: image)
      .resizable()
      .aspectRatio(contentMode: .fit)
      .frame(width:width, height:height)
      .onReceive(imageLoader.didChange) { data in
        self.image = UIImage(data: data) ?? UIImage()
      }
  }
}

class ImageLoader: ObservableObject {
  var didChange = PassthroughSubject<Data, Never>()
  var data = Data() {
    didSet {
      didChange.send(data)
    }
  }
  
  init(urlString:String) {
    guard let url = URL(string: urlString) else { return }
    let task = URLSession.shared.dataTask(with: url) { [weak self] data, response, error in
      guard let data = data, self != nil else { return }
      DispatchQueue.main.async { [self]
        self?.data = data
      }
    }
    task.resume()
  }
}
