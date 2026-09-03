import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity, Switch } from 'react-native';
import { useDispatch } from 'react-redux';
import { logout } from '../auth/authSlice';

const SettingsScreen = () => {
  const dispatch = useDispatch();
  const [isHindi, setIsHindi] = React.useState(false);

  return (
    <View style={styles.container}>
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Preferences</Text>
        <View style={styles.row}>
          <Text style={styles.rowText}>Use Hindi Language (हिंदी)</Text>
          <Switch value={isHindi} onValueChange={setIsHindi} />
        </View>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Account</Text>
        <TouchableOpacity style={styles.row}>
          <Text style={styles.rowText}>Notification Settings</Text>
          <Text style={styles.arrow}>></Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.row}>
          <Text style={styles.rowText}>Privacy Policy</Text>
          <Text style={styles.arrow}>></Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.row}>
          <Text style={styles.rowText}>Terms of Service</Text>
          <Text style={styles.arrow}>></Text>
        </TouchableOpacity>
      </View>

      <TouchableOpacity
        style={styles.logoutBtn}
        onPress={() => dispatch(logout())}
      >
        <Text style={styles.logoutText}>Logout</Text>
      </TouchableOpacity>

      <Text style={styles.version}>Version 1.0.0 (Production)</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5' },
  section: { backgroundColor: '#fff', marginTop: 20, paddingHorizontal: 16 },
  sectionTitle: { fontSize: 13, color: '#999', marginVertical: 10, textTransform: 'uppercase' },
  row: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', paddingVertical: 15, borderBottomWidth: 0.5, borderBottomColor: '#eee' },
  rowText: { fontSize: 16, color: '#333' },
  arrow: { color: '#ccc', fontSize: 18 },
  logoutBtn: { marginTop: 30, backgroundColor: '#fff', padding: 15, alignItems: 'center' },
  logoutText: { color: '#d32f2f', fontSize: 16, fontWeight: 'bold' },
  version: { textAlign: 'center', color: '#999', fontSize: 12, marginTop: 20 }
});

export default SettingsScreen;
