# Copyright (c) Meta Platforms, Inc. and affiliates.
#
# This source code is licensed under the MIT license found in the
# LICENSE file in the root directory of this source tree.

sonarkit_version = '0.6.15'
Pod::Spec.new do |spec|
  spec.name = 'Sonar'
  spec.version = sonarkit_version
  spec.license = { :type => 'MIT' }
  spec.homepage = 'https://github.com/facebook/sonar'
  spec.summary = 'SonarKit core cpp code with network implementation'
  spec.authors = 'Facebook'
  spec.source = { :git => 'https://github.com/facebook/Sonar.git',
                  :tag => 'v'+sonarkit_version }
  spec.module_name = 'Sonar'
  spec.public_header_files = 'xplat/Sonar/*.h'
  spec.source_files = 'xplat/Sonar/*.{h,cpp,m,mm}'
  spec.libraries = "stdc++"
  spec.dependency 'Folly', '~>1.0'
  spec.dependency 'RSocket', '~>0.10'
  spec.compiler_flags = '-DFB_SONARKIT_ENABLED=1 -DFOLLY_NO_CONFIG -DFOLLY_MOBILE=1 -DFOLLY_USE_LIBCPP=1 -DFOLLY_HAVE_LIBGFLAGS=0 -DFOLLY_HAVE_LIBJEMALLOC=0 -DFOLLY_HAVE_PREADV=0 -DFOLLY_HAVE_PWRITEV=0 -DFOLLY_HAVE_TFO=0 -DFOLLY_USE_SYMBOLIZER=0 -Wall
    -std=c++14
    -Wno-global-constructors'
  spec.platforms = { :ios => "8.0" }
  spec.pod_target_xcconfig = { "USE_HEADERMAP" => "NO",
                               "CLANG_CXX_LANGUAGE_STANDARD" => "c++14",
                               "HEADER_SEARCH_PATHS" => "\"$(PODS_TARGET_SRCROOT)\" \"$(PODS_ROOT)/boost-for-react-native\" \"$(PODS_ROOT)/RSocket\" \"$(PODS_ROOT)/DoubleConversion\"" }
end
