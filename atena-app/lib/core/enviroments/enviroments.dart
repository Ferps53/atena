import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:riverpod/riverpod.dart';

class Environment {
  final String _dbPath;
  final int _dbVersion;
  final String _basicUsername;
  final String _basicPassword;

  Environment._({
    required String? dbPath,
    required int? dbVersion,
    required String? basicUsername,
    required String? basicPassword,
  }) : assert(dbPath != null, 'DbPath not found in .env'),
       assert(dbVersion != null, 'dbVersion not found in .env'),
       assert(basicUsername != null, 'basicUsername not found in .env'),
       assert(basicPassword != null, 'basicPassword not found in .env'),
       _dbPath = dbPath!,
       _dbVersion = dbVersion!,
       _basicUsername = basicUsername!,
       _basicPassword = basicPassword!;

  factory Environment.fromEnvFile(final Map<String, String> envMap) {
    return Environment._(
      dbPath: envMap['DB_PATH'],
      dbVersion: int.tryParse(envMap['DB_VERSION'] ?? ''),
      basicUsername: envMap['BASIC_USERNAME'],
      basicPassword: envMap['BASIC_PASSWORD'],
    );
  }

  String get dbPath => _dbPath;

  int get dbVersion => _dbVersion;

  String get basicUsername => _basicUsername;

  String get basicPassword => _basicPassword;
}

final enviromentProvider = FutureProvider((ref) async {
  const String flavor = String.fromEnvironment('FLAVOR', defaultValue: 'dev');

  String envFile = 'env/.env.$flavor';
  await dotenv.load(fileName: envFile);

  return Environment.fromEnvFile(dotenv.env);
});
