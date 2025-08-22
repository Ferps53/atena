import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:atena/core/database/database.dart';
import 'package:atena/core/home/home_screen.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  final db = await DatabaseProvider().database;

  log(db.path);
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Atena',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.lightBlue),
      ),
      home: const HomeScreen(),
    );
  }
}
