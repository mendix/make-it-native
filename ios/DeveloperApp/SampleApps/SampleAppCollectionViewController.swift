import UIKit

class SampleAppCollectionViewController: UICollectionViewController {
    private let cellIdentifier = "SampleAppCollectionViewCell"
    private var itemsPerRow: CGFloat = 2
    private let sectionInsets = UIEdgeInsets(top: 16.0, left: 16.0, bottom: 16.0, right: 16.0)
    private let config = SampleAppsConfig()
    private var sampleApps = [SampleApp]()
    private var currentSelectedRow = -1

    override func viewDidLoad() {
        super.viewDidLoad()

        sampleApps = config.read()
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        applyMendixLogoNavigationBar(self)
    }

    override func viewWillTransition(to size: CGSize, with coordinator: UIViewControllerTransitionCoordinator) {
        super.viewWillTransition(to: size, with: coordinator)

        updateUiOnRotate(size)
        applyMendixLogoNavigationBar(self)
    }

    override func viewWillLayoutSubviews() {
        updateUiOnRotate(view.frame.size)
        
        super.viewWillLayoutSubviews()
    }

    override func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }

    override func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return sampleApps.count
    }

    override func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        guard let cell = collectionView.dequeueReusableCell(withReuseIdentifier: cellIdentifier, for: indexPath) as? SampleAppCollectionViewCell else {
            fatalError()
        }

        let sampleApp = sampleApps[indexPath.row]
        cell.imageView.image = (sampleApp.backgroundImageName != nil) ? UIImage(named: sampleApp.backgroundImageName!) : nil
        cell.imageView.layer.cornerRadius = 8.0
        cell.titleLabel.text = sampleApp.name
        cell.descriptionLabel.text = sampleApp.description ?? "Mendix sample app"

        cell.layer.cornerRadius = 8.0
        return cell
    }

    override func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        currentSelectedRow = indexPath.row
        let appDelegate = (UIApplication.shared.delegate as! AppDelegate)
        appDelegate.previewingSampleApp = true
        performSegue(withIdentifier: "OpenMendixApp", sender: self)
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.destination is MendixAppViewController {
            let sampleApp = sampleApps[currentSelectedRow]

            AppPreferences.devMode(false)
            let runtimeUrl = AppUrl.forRuntime(sampleApp.runtimeUrl)!
            let bundleUrl = Bundle.main.url(forResource: "Bundles/\(sampleApp.id)/index", withExtension: "bundle") ?? AppUrl.forBundle(sampleApp.runtimeUrl, port: AppPreferences.getRemoteDebuggingPackagerPort(), isDebuggingRemotely: false, isDevModeEnabled: false)

            ReactNative.instance.setup(MendixApp(sampleApp.id, bundleUrl: bundleUrl!, runtimeUrl: runtimeUrl, warningsFilter: WarningsFilter.none, isDeveloperApp: false, clearDataAtLaunch: true, reactLoading: UIStoryboard(name: "LaunchScreen", bundle: nil), enableThreeFingerGestures: true), launchOptions: nil)
        }
    }

    private func updateUiOnRotate(_ size: CGSize) {
        let isLandscape = size.width > size.height
        var _itemsPerRow: CGFloat
        if !isLandscape && size.width > 600 {
            _itemsPerRow = 3
        } else {
            _itemsPerRow = isLandscape ? 4 : 2
        }

        if _itemsPerRow != itemsPerRow {
            itemsPerRow = _itemsPerRow
            collectionViewLayout.invalidateLayout()
        }
    }
}

extension SampleAppCollectionViewController : UICollectionViewDelegateFlowLayout {
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        let paddingSpace = sectionInsets.left * (itemsPerRow + 1)
        let availableWidth = view.frame.width - paddingSpace
        let widthPerItem = availableWidth / itemsPerRow
        return CGSize(width: widthPerItem, height: widthPerItem * 1.35)
    }

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForSectionAt section: Int) -> UIEdgeInsets {
        return sectionInsets
    }

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return sectionInsets.left
    }
}
